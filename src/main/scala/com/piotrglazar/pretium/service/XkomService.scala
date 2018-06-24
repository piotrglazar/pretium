package com.piotrglazar.pretium.service
import com.piotrglazar.pretium.api
import com.piotrglazar.pretium.api.ItemSourceName
import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import com.piotrglazar.pretium.api.clients.XkomClient

import scala.concurrent.{ExecutionContext, Future}

class XkomService(private val client: XkomClient,
                  private val xkomPageParser: XkomPageParser)
                 (implicit private val executionContext: ExecutionContext) extends ItemService {

  override def findPrice(itemName: api.ItemName, url: String): Future[BigDecimal] = {
    client.fetch(url)
      .map(xkomPageParser.extractPrice)
      .map(_.get)
  }

  override def supports: ItemSourceName = ItemSourceName.XKOM
}
