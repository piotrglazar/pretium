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

  case class ItemPrice(name: ItemName, prices: List[ItemSourcePrice])

  case class ItemQuantity(name: ItemName, quantity: Option[Int]) {
    def getQuantity: Int = quantity.getOrElse(1)
  }

  case class ItemQuery(items: List[Item], task: List[ItemQuantity])

  case class ItemQueryResponse(items: List[ItemPrice])
}
