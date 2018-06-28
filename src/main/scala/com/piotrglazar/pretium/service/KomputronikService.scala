package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import com.piotrglazar.pretium.api.clients.WebClient
import com.piotrglazar.pretium.api.{ItemName, ItemSourceName}

import scala.concurrent.{ExecutionContext, Future}

class KomputronikService(private val client: WebClient,
                          private val komputronikPageParser: KomputronikPageParser)
                        (implicit private val executionContext: ExecutionContext) extends ItemService {

  override def findPrice(itemName: ItemName, url: String): Future[BigDecimal] = {
    client.fetch(url)
      .map(komputronikPageParser.extractPrice)
      .map(_.get)
  }

  override def supports: ItemSourceName = ItemSourceName.KOMPUTRONIK
}
