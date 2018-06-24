package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api.ItemName
import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName

import scala.concurrent.Future

trait ItemService {

  def findPrice(itemName: ItemName, url: String): Future[BigDecimal]

  def supports: ItemSourceName
}
