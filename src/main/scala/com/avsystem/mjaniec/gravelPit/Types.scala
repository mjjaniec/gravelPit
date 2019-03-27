package com.avsystem.mjaniec.gravelPit

import squants.mass.Mass
import squants.space.Length

case class Granulation(min: Length, max: Length) {
  override def hashCode(): Int = min.hashCode()

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case g: Granulation => g.min == min
      case _ => false
    }
  }
}

object Granulation {
  // For comparing we use only lower bound - we can do that thanks to assumptions
  // provided in task description
  implicit val ordering: Ordering[Granulation] = Ordering.by[Granulation, Length](_.min)
}

case class HeapLabel(string: String) extends AnyVal

case class Heap(granulation: Granulation, label: HeapLabel)

case class Product(granulation: Granulation, amount: Mass)

case class Order(product: Product)

sealed trait OrderRealisation

case class CollectGravel(fromHeap: HeapLabel, amount: Mass)

object OrderRealisation {
  case object Impossible extends OrderRealisation
  case class Possible(heaps: List[CollectGravel]) extends OrderRealisation
}

