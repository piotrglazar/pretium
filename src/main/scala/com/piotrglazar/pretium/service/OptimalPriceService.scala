package com.piotrglazar.pretium.service

import akka.stream.ActorAttributes.supervisionStrategy
import akka.stream.Materializer
import akka.stream.Supervision.resumingDecider
import akka.stream.scaladsl.{Sink, Source}
import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import com.piotrglazar.pretium.api.{Item, ItemPrice, ItemQuantity, ItemSourcePrice}
import com.piotrglazar.pretium.service.OptimalPriceService.parallelismFactor
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object OptimalPriceService {
  private val parallelismFactor = 4
}

class OptimalPriceService(sources: List[ItemService])
                         (implicit private val executionContext: ExecutionContext, private val materializer: Materializer) extends LazyLogging {

  private val sourcesMap: Map[ItemSourceName, ItemService] = sources.map(s => s.supports -> s)(collection.breakOut)

  def findOptimalPrice(items: List[ItemQuantity], itemDefinitions: List[Item]): Future[List[ItemPrice]] = {
    // only find necessary items
    val necessaryItemNames = items.groupBy(_.name).mapValues(_.head).map(identity)
    val necessaryItemDefinitions = itemDefinitions.filter(i => necessaryItemNames.contains(i.name))

    Future.sequence(necessaryItemDefinitions.map(findOptimalPrice)).map(_.flatten.map { itemPrice =>
      val quantity = necessaryItemNames(itemPrice.name).getQuantity
      if (quantity == 1) {
        itemPrice
      } else {
        ItemPrice(itemPrice.name, itemPrice.prices.map(isp => ItemSourcePrice(isp.itemSourceName, isp.price * quantity)))
      }
    })
  }

  private def findOptimalPrice(item: Item): Future[Option[ItemPrice]] = {
    // just find the lowest
    val futurePrices = fetchPricesInFlow(item)

    logFoundPrices(item, futurePrices)
    futurePrices.map(ps =>
      if (ps.isEmpty) {
        None
      } else {
        val minPrice = ps.minBy(_.price).price
        Some(ItemPrice(item.name, ps.filter(_.price == minPrice)))
      }
    )
  }

  private def logFoundPrices(item: Item, allPrices: Future[List[ItemSourcePrice]]): Unit = {
    allPrices.onComplete {
      case Success(prices) =>
        logger.info(s"Found prices for ${item.name.value} ${prices.mkString(", ")}")
      case Failure(t) =>
        logger.error(s"Failed to find prices for ${item.name.value}", t)
    }
  }

  private def fetchPricesInFlow(item: Item): Future[List[ItemSourcePrice]] = {
    val applicableSources = item.sources.filter(is => sourcesMap.contains(is.sourceName))

    if (applicableSources.isEmpty) {
      val msg = s"No source found for item $item"
      logger.error(msg)
      Future.failed(new IllegalStateException(msg))
    } else {
      Source(item.sources)
        .filter(is => sourcesMap.contains(is.sourceName))
        .map(is => (is.path, sourcesMap(is.sourceName)))
        .mapAsyncUnordered(sourcesMap.size * parallelismFactor) { case (path, service) =>
          service.findPrice(path).map(ItemSourcePrice(service.supports, _))
        }
        .withAttributes(supervisionStrategy(resumingDecider))
        .runWith(Sink.collection)
    }
  }
}
