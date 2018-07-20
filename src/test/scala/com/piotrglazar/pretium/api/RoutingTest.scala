package com.piotrglazar.pretium.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.PredefinedFromEntityUnmarshallers
import com.piotrglazar.pretium.api.ItemSourceName.XKOM
import com.piotrglazar.pretium.service.OptimalPriceService
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import io.circe.generic.auto._
import io.circe.syntax._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.Future

class RoutingTest extends FlatSpec with ScalatestRouteTest with Matchers with MockitoSugar with BeforeAndAfter
  // needed for unmarshalling as string
  with PredefinedFromEntityUnmarshallers with FailFastCirceSupport with PretiumJsonSupport {

  private var optimalPriceService: OptimalPriceService = _

  private var routing: Routing = _

  before {
    optimalPriceService = mock[OptimalPriceService]
    routing = new Routing(optimalPriceService)
  }

  it should "accept health check" in {
    // when
    Get("/health") ~> routing.route ~> check {

      // then
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual """{"status": "OK"}"""
    }
  }

  it should "accept valid request with item quantity" in {
    // given
    val item = Item(ItemName("intel cpu"), List(ItemSource("/some-path", XKOM)))
    val query = ItemQuery(List(item), List(ItemQuantity(item.name, None)))
    given(optimalPriceService.findOptimalPrice(query.task, query.items))
      .willReturn(Future.successful(OptimalPrices(List(ItemPrice(item.name, List(ItemSourcePrice(XKOM, 20.00)), 1)), 20.00)))

    // when
    Post("/prices", query.asJson) ~> routing.route ~> check {

      // then
      status shouldEqual StatusCodes.OK
      val itemQueryResponse = responseAs[ItemQueryResponse]
      itemQueryResponse.items.head.prices.head.price shouldEqual BigDecimal("20.00")
      itemQueryResponse.items.head.quantity shouldEqual 1
      itemQueryResponse.total shouldEqual BigDecimal("20.00")
    }
  }

  it should "accept valid request with replacement" in {
    // given
    val item = Item(ItemName("intel cpu"), List(ItemSource("/some-path", XKOM)))
    val query = ItemQuery(List(item), List(Replacement(List(ItemQuantity(item.name, None)))))
    given(optimalPriceService.findOptimalPrice(query.task, query.items))
      .willReturn(Future.successful(OptimalPrices(List(ItemPrice(item.name, List(ItemSourcePrice(XKOM, 20.00)), 1)), 20.00)))

    // when
    Post("/prices", query.asJson) ~> routing.route ~> check {

      // then
      status shouldEqual StatusCodes.OK
      val itemQueryResponse = responseAs[ItemQueryResponse]
      itemQueryResponse.items.head.prices.head.price shouldEqual BigDecimal("20.00")
      itemQueryResponse.items.head.quantity shouldEqual 1
      itemQueryResponse.total shouldEqual BigDecimal("20.00")
    }
  }

  it should "accept valid request with replacement and item quantity" in {
    // given
    val item = Item(ItemName("intel cpu"), List(ItemSource("/some-path", XKOM)))
    val replacedItem1 = Item(ItemName("nvidia gpu"), List(ItemSource("/gpu-path-n", XKOM)))
    val replacedItem2 = Item(ItemName("amd gpu"), List(ItemSource("/gpu-path-a", XKOM)))
    val query = ItemQuery(List(item, replacedItem1, replacedItem2), List(ItemQuantity(item.name, None),
      Replacement(List(ItemQuantity(replacedItem1.name, None), ItemQuantity(replacedItem2.name, None)))))
    given(optimalPriceService.findOptimalPrice(query.task, query.items))
      .willReturn(Future.successful(OptimalPrices(List(ItemPrice(item.name, List(ItemSourcePrice(XKOM, 20.00)), 1)), 20.00)))

    // when
    Post("/prices", query.asJson) ~> routing.route ~> check {

      // then
      status shouldEqual StatusCodes.OK
      val itemQueryResponse = responseAs[ItemQueryResponse]
      itemQueryResponse.items.head.prices.head.price shouldEqual BigDecimal("20.00")
      itemQueryResponse.items.head.quantity shouldEqual 1
      itemQueryResponse.total shouldEqual BigDecimal("20.00")
    }
  }
}
