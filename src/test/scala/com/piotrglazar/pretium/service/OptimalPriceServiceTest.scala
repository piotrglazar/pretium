package com.piotrglazar.pretium.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import com.piotrglazar.pretium.api.ItemSourceName.{TEST, XKOM}
import com.piotrglazar.pretium.api.{Item, ItemName, ItemQuantity, ItemSource}
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class OptimalPriceServiceTest extends FlatSpec with BeforeAndAfter with MockitoSugar with Matchers with BeforeAndAfterAll {

  implicit private val actorSystem: ActorSystem = ActorSystem("OptimalPriceService")
  implicit private val materializer: ActorMaterializer = ActorMaterializer()
  implicit private val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  private var service: OptimalPriceService = _
  private var testService: ItemService = _

  before {
    testService = mock[ItemService]
    given(testService.supports).willReturn(TEST)
  }

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(actorSystem, verifySystemShutdown = true)
  }

  it should "fail when there is no source for item" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", XKOM)))
    service = new OptimalPriceService(List(testService))

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
    val xkomService = mock[ItemService]
    given(xkomService.supports).willReturn(XKOM)
    given(testService.findPrice("/path2")).willReturn(Future.successful(BigDecimal(10)))
    given(xkomService.findPrice("/path")).willReturn(Future.successful(BigDecimal(5)))
    service = new OptimalPriceService(List(testService, xkomService))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, None)), List(item))

    // then
    val itemPrice = Await.result(eventualPrice, 1 second).head
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
    val xkomService = mock[ItemService]
    given(xkomService.supports).willReturn(XKOM)
    given(testService.findPrice("/path2")).willReturn(Future.successful(BigDecimal(5)))
    given(xkomService.findPrice("/path")).willReturn(Future.successful(BigDecimal(5)))
    service = new OptimalPriceService(List(testService, xkomService))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, None)), List(item))

    // then
    val itemPrice = Await.result(eventualPrice, 1 second).head
    itemPrice.prices should have size 2
    itemPrice.prices.map(_.price).distinct shouldEqual List(BigDecimal(5))
  }

  it should "not fail when one source fails to fetch data" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", XKOM), ItemSource("/path2", TEST)))
    val xkomService = mock[ItemService]
    given(xkomService.supports).willReturn(XKOM)
    given(testService.findPrice("/path2")).willReturn(Future.failed(new RuntimeException("expected")))
    given(xkomService.findPrice("/path")).willReturn(Future.successful(BigDecimal(5)))
    service = new OptimalPriceService(List(testService, xkomService))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, None)), List(item))

    // then
    val itemPrice = Await.result(eventualPrice, 1 second).head
    itemPrice.prices should have size 1
  }

  it should "process only items that are listed in tasks" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))
    val item2 = Item(ItemName("cool ram"), List(ItemSource("/path2", TEST)))
    given(testService.findPrice("/path")).willReturn(Future.successful(BigDecimal(5)))
    service = new OptimalPriceService(List(testService))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, None)), List(item, item2))

    // then
    val itemPrices = Await.result(eventualPrice, 1 second)
    itemPrices should have size 1
    itemPrices.head.name shouldEqual item.name
  }

  it should "multiply item price by quantity" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))
    given(testService.findPrice("/path")).willReturn(Future.successful(BigDecimal(5)))
    service = new OptimalPriceService(List(testService))

    // when
    val eventualPrice = service.findOptimalPrice(List(ItemQuantity(item.name, Some(3))), List(item))

    // then
    val itemPrice = Await.result(eventualPrice, 1 second).head
    itemPrice.prices.head.price shouldEqual BigDecimal(15)
  }

  it should "do nothing when task list is empty" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))
    service = new OptimalPriceService(List(testService))

    // when
    val eventualPrice = service.findOptimalPrice(List.empty, List(item))

    // then
    val itemPrices = Await.result(eventualPrice, 1 second)
    itemPrices should be an 'empty
  }

  it should "not fail when processing of one item fails" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))
    val item2 = Item(ItemName("cool ram"), List(ItemSource("/path2", XKOM)))
    val xkomService = mock[ItemService]
    given(xkomService.supports).willReturn(XKOM)
    given(xkomService.findPrice("/path2")).willReturn(Future.failed(new RuntimeException("expected")))
    given(testService.findPrice("/path")).willReturn(Future.successful(BigDecimal(5)))
    service = new OptimalPriceService(List(testService, xkomService))

    // when
    val eventualPrices = service.findOptimalPrice(
      List(ItemQuantity(item.name, Some(1)), ItemQuantity(item2.name, Some(1))),
      List(item, item2)
    )

    val itemPrices = Await.result(eventualPrices, 1 second)
    itemPrices should have size 1
    itemPrices.head.name shouldEqual item.name
  }
}
