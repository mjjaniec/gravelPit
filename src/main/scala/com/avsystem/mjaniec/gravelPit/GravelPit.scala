package com.avsystem.mjaniec.gravelPit

import squants.Mass

import scala.collection.mutable

class GravelPit {

  private val heapMasses = new mutable.TreeMap[Granulation, Mass]()
  private val heapNames = new mutable.TreeMap[Granulation, String]()

  def production(product: Product): HeapLabel = {
    val label = heapNames.getOrElseUpdate(product.granulation, "H" + (heapMasses.size + 1))
    HeapLabel(label)
  }




}
