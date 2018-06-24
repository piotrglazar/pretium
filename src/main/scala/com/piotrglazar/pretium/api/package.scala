package com.piotrglazar.pretium

import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName

package object api {

  object ItemSourceName extends Enumeration {
    type ItemSourceName = Value
    val XKOM, TEST: Value = Value
  }

  case class ItemSource(path: String, sourceName: ItemSourceName)

  case class ItemName(value: String) extends AnyVal

  case class Item(name: ItemName, sources: List[ItemSource])

  case class ItemPrice(name: ItemName, itemSourceName: ItemSourceName, price: BigDecimal)
}
