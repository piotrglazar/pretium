package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api.ItemSourceName.{TEST, XKOM}
import com.piotrglazar.pretium.api.{Item, ItemName, ItemSource}
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

class OptimalPriceServiceTest extends FlatSpec with BeforeAndAfter with MockitoSugar with Matchers {

  private var service: OptimalPriceService = _
  private var testService: ItemService = _

  before {
    testService = mock[ItemService]
    given(testService.supports).willReturn(TEST)
  }

  it should "fail when there is no source for item" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", XKOM)))
    service = new OptimalPriceService(List(testService))

    // when
    val eventualPrice = service.findOptimalPrice(item)

    // then
    assertThrows[IllegalStateException] {
      Await.result(eventualPrice, 1 second)
    }
  }

  it should "pick the lowest price" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", XKOM), ItemSource("/path2", TEST)))
    val xkomService = mock[ItemService]
    given(xkomService.supports).willReturn(XKOM)
    given(testService.findPrice(item.name, "/path2")).willReturn(Future.successful(BigDecimal(10)))
    given(xkomService.findPrice(item.name, "/path")).willReturn(Future.successful(BigDecimal(5)))
    service = new OptimalPriceService(List(testService, xkomService))

    // when
    val eventualPrice = service.findOptimalPrice(item)

    // then
    Await.result(eventualPrice, 1 second) should have (
      'itemSourceName (XKOM),
      'price (BigDecimal(5))
    )
  }
}
