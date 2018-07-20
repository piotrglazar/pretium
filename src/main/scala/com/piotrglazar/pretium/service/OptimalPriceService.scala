package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api._
import com.piotrglazar.pretium.service.OptimalPriceService.FoundPrice
import com.piotrglazar.pretium.utils.MapUtils._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

object OptimalPriceService {
  case class FoundPrice(item: Item, prices: List[ItemSourcePrice])
}

class OptimalPriceService(private val priceFetchService: PriceFetchService)
                         (implicit private val executionContext: ExecutionContext) extends LazyLogging {

  def findOptimalPrice(tasks: List[Task], itemDefinitions: List[Item]): Future[OptimalPrices] = {
    // only find necessary items
    val necessaryItemNames = tasks.flatMap(_.items).groupBy(_.name).singleValue()
    val necessaryItemDefinitions = itemDefinitions.filter(i => necessaryItemNames.contains(i.name))

    Future.sequence(necessaryItemDefinitions.map(findOptimalPrice))
      .map(_.collect { case Some(foundPrice) =>
        val quantity = necessaryItemNames(foundPrice.item.name).getQuantity

        ItemPrice(
          foundPrice.item.name,
          foundPrice.prices.map(isp => ItemSourcePrice(isp.itemSourceName, isp.price * quantity)),
          quantity
        )
      })
      .map(_.groupBy(_.name).singleValue())
      .map { itemPrices =>
        tasks.flatMap {
          case i: ItemQuantity => itemPrices.get(i.name)
          case Replacement(items) =>
            val itemsWithPrices = items.flatMap(i => itemPrices.get(i.name))
            if (itemsWithPrices.nonEmpty) {
              Some(itemsWithPrices.minBy(_.prices.minBy(_.price).price))
            } else {
              None
            }
        }
      }
      .map(prices => OptimalPrices(prices, prices.map(_.prices.head).map(_.price).sum))
  }

  private def findOptimalPrice(item: Item): Future[Option[FoundPrice]] = {
    // just find the lowest prices
    val futurePrices = priceFetchService.fetchPricesInFlow(item)

    futurePrices.map(ps =>
      if (ps.isEmpty) {
        None
      } else {
        val minPrice = ps.minBy(_.price).price
        Some(FoundPrice(item, ps.filter(_.price == minPrice)))
      }
    )
  }
}
