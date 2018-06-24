package com.piotrglazar.pretium.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.PredefinedFromEntityUnmarshallers
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

  it should "accept valid request" in {
    // given
    val item = Item(ItemName("intel cpu"), List(ItemSource("/some-path", ItemSourceName.XKOM)))
    given(optimalPriceService.findOptimalPrice(item))
      .willReturn(Future.successful(ItemPrice(item.name, ItemSourceName.XKOM, 20.00)))

    // when
    Post("/prices", item.asJson) ~> routing.route ~> check {

      // then
      status shouldEqual StatusCodes.OK
      responseAs[ItemPrice].price shouldEqual BigDecimal("20.00")
    }
  }
}