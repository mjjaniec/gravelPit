package com.avsystem.mjaniec.gravelPit

import squants.mass.{Kilograms, Mass}

import scala.collection.mutable

class GravelPit {

  private val Zero = Kilograms(0)
  private val heapMasses = new mutable.TreeMap[Granulation, Mass]()
  private val heapLabels = new mutable.TreeMap[Granulation, HeapLabel]()

  def production(product: Product): HeapLabel = {
    val label = heapLabels.getOrElseUpdate(product.granulation, HeapLabel("H" + (heapMasses.size + 1)))
    val heapMass = heapMasses.getOrElse(product.granulation, Zero)
    heapMasses.put(product.granulation, heapMass + product.amount)
    label
  }

  def order(order: Order): OrderRealisation = {

    case class HeapItem(mass: Mass, label: HeapLabel)

    implicit val heapItemOrdering: Ordering[HeapItem] = Ordering.by[HeapItem, Mass](_.mass)

    val queue = mutable.PriorityQueue.empty[HeapItem]

    // Ordering uses only min
    heapMasses.iteratorFrom(order.product.granulation)
      .takeWhile { case (granulation, _) =>
        granulation.max <= order.product.granulation.max
      }.foreach { case (granulation, mass) =>
      queue.enqueue(HeapItem(mass, heapLabels(granulation)))
    }

    var currentTop = queue.headOption.map(_.mass).getOrElse(Zero)
    val activeHeaps = mutable.Map.empty[HeapLabel, Mass]
    val orderHeaps = mutable.Map.empty[HeapLabel, Mass]

    var massLeft = order.product.amount
    while (massLeft > Zero) {
      if (queue.isEmpty) {
        return OrderRealisation.Impossible
      }
      while (queue.headOption.exists(_.mass == currentTop)) {
        val next = queue.dequeue()
        activeHeaps.put(next.label, next.mass)
      }
      currentTop = queue.headOption.map(_.mass).getOrElse(Zero)

      val maxCollect = activeHeaps.size * (activeHeaps.head._2 - currentTop)
      val toCollect = implicitly[Ordering[Mass]].min(massLeft, maxCollect)
      val perHeap = toCollect / activeHeaps.size

      activeHeaps.foreach { case (label, mass) =>
        orderHeaps.put(label, orderHeaps.getOrElse(label, Zero) + perHeap)
        activeHeaps.update(label, mass - perHeap)
      }

      massLeft -= toCollect
    }

    OrderRealisation.Possible(orderHeaps.map { case (label, mass) =>
      CollectGravel(label, mass)
    }.toList)
  }

}
