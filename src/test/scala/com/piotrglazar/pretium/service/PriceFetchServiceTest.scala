package com.piotrglazar.pretium.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import com.piotrglazar.pretium.api.{Item, ItemName, ItemSource}
import com.piotrglazar.pretium.api.ItemSourceName.{TEST, XKOM}
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class PriceFetchServiceTest extends FlatSpec with BeforeAndAfter with MockitoSugar with Matchers with BeforeAndAfterAll {

  implicit private val actorSystem: ActorSystem = ActorSystem("PriceFetchService")
  implicit private val materializer: ActorMaterializer = ActorMaterializer()
  implicit private val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

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
    val priceFetchService = new PriceFetchService(List(testService))

    // when
    val eventualPrice = priceFetchService.fetchPricesInFlow(item)

    // then
    assertThrows[IllegalStateException] {
      Await.result(eventualPrice, 1 second)
    }
  }

  it should "not fail when one source fails to fetch data" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", XKOM), ItemSource("/path2", TEST)))
    val xkomService = mock[ItemService]
    given(xkomService.supports).willReturn(XKOM)
    given(testService.findPrice("/path2")).willReturn(Future.failed(new RuntimeException("expected")))
    given(xkomService.findPrice("/path")).willReturn(Future.successful(BigDecimal(5)))
    val priceFetchService = new PriceFetchService(List(testService, xkomService))

    // when
    val eventualPrice = priceFetchService.fetchPricesInFlow(item)

    // then
    val itemPrice = Await.result(eventualPrice, 1 second)
    itemPrice should have size 1
    itemPrice.head should have (
      'price (BigDecimal(5)),
      'itemSourceName (XKOM)
    )
  }

  it should "return empty result when fails to fetch data" in {
    // given
    val item = Item(ItemName("cool gpu"), List(ItemSource("/path", TEST)))
    given(testService.findPrice("/path")).willReturn(Future.failed(new RuntimeException("expected")))
    val priceFetchService = new PriceFetchService(List(testService, testService))

    // when
    val eventualPrice = priceFetchService.fetchPricesInFlow(item)

    // then
    val itemPrice = Await.result(eventualPrice, 1 second)
    itemPrice should be an 'empty
  }
}
