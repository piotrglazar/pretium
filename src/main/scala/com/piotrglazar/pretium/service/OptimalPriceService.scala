package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import com.piotrglazar.pretium.api.{Item, ItemPrice, ItemSource}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class OptimalPriceService(sources: List[ItemService])(implicit private val executionContext: ExecutionContext) extends LazyLogging {

  private val sourcesMap: Map[ItemSourceName, ItemService] = sources.map(s => s.supports -> s)(collection.breakOut)

  def findOptimalPrice(item: Item): Future[ItemPrice] = {
    // just find the lowest
    val futurePrices = item.sources.collect { case ItemSource(path, itemSourceName) if sourcesMap.contains(itemSourceName) =>
    val service = sourcesMap(itemSourceName)
      service.findPrice(item.name, path).map(ItemPrice(item.name, itemSourceName, _))
    }

    if (futurePrices.nonEmpty) {
      Future.sequence(futurePrices).map(_.minBy(_.price))
    } else {
      val msg = s"No source found for item $item"
      logger.error(msg)
      Future.failed(new IllegalStateException(msg))
    }
  }
}
