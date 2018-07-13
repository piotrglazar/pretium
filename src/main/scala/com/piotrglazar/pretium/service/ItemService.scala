package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName

import scala.concurrent.Future

trait ItemService {

  def findPrice(url: String): Future[BigDecimal]

  def supports: ItemSourceName
}
