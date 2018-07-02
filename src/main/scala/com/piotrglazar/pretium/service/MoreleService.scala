package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api
import com.piotrglazar.pretium.api.ItemSourceName
import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import com.piotrglazar.pretium.api.clients.WebClient

import scala.concurrent.{ExecutionContext, Future}

class MoreleService(private val client: WebClient,
                    private val morelePageParser: MorelePageParser)
                   (implicit private val executionContext: ExecutionContext) extends ItemService {

  override def findPrice(url: String): Future[BigDecimal] = {
    client.fetch(url)
      .map(morelePageParser.extractPrice)
      .map(_.get)
  }

  override def supports: ItemSourceName = ItemSourceName.MORELE
}
