package com.piotrglazar.pretium.api.clients

import java.io.IOException

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import com.piotrglazar.pretium.api.ClientConfig
import com.xebialabs.restito.builder.stub.StubHttp
import com.xebialabs.restito.semantics.Action.{status, stringContent}
import com.xebialabs.restito.semantics.Condition.get
import com.xebialabs.restito.server.StubServer
import org.glassfish.grizzly.http.util.HttpStatus
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.language.postfixOps

class XkomClientTest extends FlatSpec with BeforeAndAfterAll with BeforeAndAfter with Matchers {

  private val url = "/item"

  private implicit val system: ActorSystem = ActorSystem("XkomClientTest")

  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  private implicit val executor: ExecutionContextExecutor = system.dispatcher

  private var server: StubServer = _

  private var client: XkomClient = _

  before {
    server = new StubServer().secured().run()
    client = XkomClient(new ClientConfig {
      override def host: String = "localhost"

      override def port: Int = server.getPort
    })
  }

  it should "fetch web page" in {
    // given
    StubHttp.whenHttp(server)
      .`match`(get(url))
      .`then`(stringContent("cool item"))
      .mustHappen()

    // when
    val result = client.fetch(url)

    // then
    Await.result(result, 1 second) shouldEqual "cool item"
  }

  it should "propagate error when unable to fetch web page" in {
    // given
    StubHttp.whenHttp(server)
      .`match`(get(url))
      .`then`(status(HttpStatus.INTERNAL_SERVER_ERROR_500))
      .mustHappen()

    // when
    val result = client.fetch(url)

    // then
    assertThrows[IOException] {
      Await.result(result, 1 second)
    }
  }

  after {
    server.stop()
  }

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system, verifySystemShutdown =  true)
  }
}
