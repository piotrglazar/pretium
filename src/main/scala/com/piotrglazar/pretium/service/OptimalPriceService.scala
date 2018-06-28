package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import com.piotrglazar.pretium.api.{Item, ItemPrice, ItemSource, ItemSourcePrice}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class OptimalPriceService(sources: List[ItemService])(implicit private val executionContext: ExecutionContext) extends LazyLogging {

  private val sourcesMap: Map[ItemSourceName, ItemService] = sources.map(s => s.supports -> s)(collection.breakOut)

  def findOptimalPrice(item: Item): Future[ItemPrice] = {
    // just find the lowest
    val futurePrices = item.sources.collect { case ItemSource(path, itemSourceName) if sourcesMap.contains(itemSourceName) =>
      val service = sourcesMap(itemSourceName)
      service.findPrice(item.name, path).map(ItemSourcePrice(itemSourceName, _))
    }

    if (futurePrices.nonEmpty) {
      val allPrices = Future.sequence(futurePrices)
      logFoundPrices(item, allPrices)
      allPrices.map(_.minBy(_.price)).flatMap(minPrice =>
        allPrices.map(a => ItemPrice(item.name, a.filter(_.price == minPrice.price)))
      )
    } else {
      val msg = s"No source found for item $item"
      logger.error(msg)
      Future.failed(new IllegalStateException(msg))
    }
  }

  private def logFoundPrices(item: Item, allPrices: Future[List[ItemSourcePrice]]): Unit = {
    allPrices.onComplete {
      case Success(prices) =>
        logger.info(s"Found prices for ${item.name.value} ${prices.mkString(", ")}")
      case Failure(t) =>
        logger.error(s"Failed to find prices for ${item.name.value}", t)
    }
  }
}
