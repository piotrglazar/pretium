package com.piotrglazar.pretium.service

import akka.stream.ActorAttributes.supervisionStrategy
import akka.stream.Materializer
import akka.stream.Supervision.resumingDecider
import akka.stream.scaladsl.{Sink, Source}
import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import com.piotrglazar.pretium.api.{Item, ItemSourcePrice}
import com.piotrglazar.pretium.service.PriceFetchService.parallelismFactor
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object PriceFetchService {
  private val parallelismFactor = 4
}

class PriceFetchService(sources: List[ItemService])
                       (implicit private val executionContext: ExecutionContext, private val materializer: Materializer)
  extends LazyLogging {

  private val sourcesMap: Map[ItemSourceName, ItemService] = sources.map(s => s.supports -> s)(collection.breakOut)

  def fetchPricesInFlow(item: Item): Future[List[ItemSourcePrice]] = {
    val applicableSources = item.sources.filter(is => sourcesMap.contains(is.sourceName))

    val result: Future[List[ItemSourcePrice]] = if (applicableSources.isEmpty) {
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

    logFoundPrices(item, result)

    result
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
