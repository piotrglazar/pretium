package com.piotrglazar.pretium

import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName

package object api {

  object ItemSourceName extends Enumeration {
    type ItemSourceName = Value
    val XKOM, KOMPUTRONIK, MORELE, TEST: Value = Value
  }

  case class ItemSource(path: String, sourceName: ItemSourceName)

  case class ItemName(value: String) extends AnyVal

  case class Item(name: ItemName, sources: List[ItemSource])

  case class ItemSourcePrice(itemSourceName: ItemSourceName, price: BigDecimal)

  case class ItemPrice(name: ItemName, prices: List[ItemSourcePrice], quantity: Int)

  case class OptimalPrices(items: List[ItemPrice], total: BigDecimal)

  sealed trait Task {
    def items: List[ItemQuantity]
  }

  case class Replacement(items: List[ItemQuantity]) extends Task

  case class ItemQuantity(name: ItemName, quantity: Option[Int]) extends Task {
    def getQuantity: Int = quantity.getOrElse(1)

    override def items: List[ItemQuantity] = List(this)
  }

  case class ItemQuery(items: List[Item], task: List[Task])

  // TODO, yes a duplicate of OptimalPrices
  case class ItemQueryResponse(items: List[ItemPrice], total: BigDecimal)
}
