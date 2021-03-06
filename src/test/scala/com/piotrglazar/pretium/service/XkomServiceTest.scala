package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.api.ItemName
import com.piotrglazar.pretium.api.ItemSourceName.XKOM
import com.piotrglazar.pretium.api.clients.WebClient
import org.mockito.BDDMockito.given
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Success

class XkomServiceTest extends FlatSpec with BeforeAndAfter with MockitoSugar with Matchers {

  private val itemName = ItemName("cool cpu")
  private val url = "/cool-cpu"

  private var service: XkomService = _
  private var client: WebClient = _
  private var pageParser: XkomPageParser = _

  before {
    client = mock[WebClient]
    pageParser = mock[XkomPageParser]
    service = new XkomService(client, pageParser)
  }

  it should "support xkom" in {
    // when
    val supportedSource = service.supports

    // then
    supportedSource shouldEqual XKOM
  }

  it should "find item price" in {
    // given
    given(client.fetch(url)).willReturn(Future.successful("page content"))
    given(pageParser.extractPrice("page content")).willReturn(Success(BigDecimal(20)))

    // when
    val productPrice = service.findPrice(url)

    // then
    Await.result(productPrice, 1 second) shouldEqual BigDecimal(20)
  }
}
