package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api.ItemSourceName.{TEST, XKOM}
import com.piotrglazar.pretium.api._
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class OptimalPriceServiceTest extends FlatSpec with BeforeAndAfter with MockitoSugar with Matchers {

  private var service: OptimalPriceService = _
  private var testService: ItemService = _
  private var priceFetchService: PriceFetchService = _

  before {
    priceFetchService = mock[PriceFetchService]
    service = new OptimalPriceService(priceFetchService)
    testService = mock[ItemService]
    given(testService.supports).willReturn(TEST)
  }

  it should "fail when there is no source for item" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", XKOM)))
    // expected behaviour, OptimalPriceService will propagate the exception
    given(priceFetchService.fetchPricesInFlow(item)).willReturn(Future.failed(new IllegalStateException("expected")))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, None)), List(item))

    // then
    assertThrows[IllegalStateException] {
      Await.result(eventualPrice, 1 second)
    }
  }

  it should "pick the lowest price" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", XKOM), ItemSource("/path2", TEST)))
    given(priceFetchService.fetchPricesInFlow(item)).willReturn(Future.successful(List(
      ItemSourcePrice(XKOM, BigDecimal(5)),
      ItemSourcePrice(TEST, BigDecimal(10))
    )))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, None)), List(item))

    // then
    val itemPrice = Await.result(eventualPrice, 1 second).items.head
    itemPrice.name shouldEqual item.name
    itemPrice.prices should have size 1
    itemPrice.prices.head should have (
      'itemSourceName (XKOM),
      'price (BigDecimal(5))
    )
  }

  it should "pick two lowest prices" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", XKOM), ItemSource("/path2", TEST)))
    given(priceFetchService.fetchPricesInFlow(item)).willReturn(Future.successful(List(
      ItemSourcePrice(TEST, BigDecimal(5)),
      ItemSourcePrice(XKOM, BigDecimal(5))
    )))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, None)), List(item))

    // then
    val itemPrice = Await.result(eventualPrice, 1 second).items.head
    itemPrice.prices should have size 2
    itemPrice.prices.map(_.price).distinct shouldEqual List(BigDecimal(5))
  }

  it should "process only items that are listed in tasks" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))
    val item2 = Item(ItemName("cool ram"), List(ItemSource("/path2", TEST)))
    given(priceFetchService.fetchPricesInFlow(item)).willReturn(Future.successful(List(ItemSourcePrice(TEST, BigDecimal(5)))))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, None)), List(item, item2))

    // then
    val itemPrices = Await.result(eventualPrice, 1 second).items
    itemPrices should have size 1
    itemPrices.head.name shouldEqual item.name
  }

  it should "multiply item price by quantity" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))
    given(priceFetchService.fetchPricesInFlow(item)).willReturn(Future.successful(List(ItemSourcePrice(TEST, BigDecimal(5)))))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, Some(3))), List(item))

    // then
    val itemPrice = Await.result(eventualPrice, 1 second).items.head
    itemPrice.prices.head.price shouldEqual BigDecimal(15)
    itemPrice.quantity shouldEqual 3
  }

  it should "do nothing when task list is empty" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))

    // when
    val eventualPrice = service.findOptimalPrice(List.empty, List(item))

    // then
    val itemPrices = Await.result(eventualPrice, 1 second).items
    itemPrices should be an 'empty
  }

  it should "not fail when processing of one item fails" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))
    val item2 = Item(ItemName("cool ram"), List(ItemSource("/path2", XKOM)))
    given(priceFetchService.fetchPricesInFlow(item)).willReturn(Future.successful(List(ItemSourcePrice(TEST, BigDecimal(5)))))
    given(priceFetchService.fetchPricesInFlow(item2)).willReturn(Future.successful(List.empty))

    // when
    val eventualPrices = service.findOptimalPrice(
      List(ItemQuantity(item.name, Some(1)), ItemQuantity(item2.name, Some(1))),
      List(item, item2)
    )

    // then
    val itemPrices = Await.result(eventualPrices, 1 second).items
    itemPrices should have size 1
    itemPrices.head.name shouldEqual item.name
  }

  it should "pick lowest price from replacement item" in {
    // given
    val item1 = Item(ItemName("cool ram big"), List(ItemSource("/path1", XKOM)))
    val item2 = Item(ItemName("cool ram small"), List(ItemSource("/path2", XKOM)))
    given(priceFetchService.fetchPricesInFlow(item1)).willReturn(Future.successful(List(ItemSourcePrice(XKOM, BigDecimal(10)))))
    given(priceFetchService.fetchPricesInFlow(item2)).willReturn(Future.successful(List(ItemSourcePrice(XKOM, BigDecimal(6)))))

    // when
    val eventualPrices = service.findOptimalPrice(
      List(Replacement(List(ItemQuantity(item1.name, Some(1)), ItemQuantity(item2.name, Some(2))))),
      List(item1, item2)
    )

    // then
    val itemPrices = Await.result(eventualPrices, 1 second).items
    itemPrices should have size 1
    itemPrices.head.name shouldEqual item1.name
    itemPrices.head.prices.head.price shouldEqual BigDecimal(10)
  }
}
