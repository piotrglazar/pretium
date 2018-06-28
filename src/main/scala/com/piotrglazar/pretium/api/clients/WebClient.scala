package com.piotrglazar.pretium.api.clients

import java.io.IOException
import java.security.cert.X509Certificate

import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{ConnectionContext, Http}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.piotrglazar.pretium.api.ClientConfig
import com.typesafe.scalalogging.LazyLogging
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}

import scala.concurrent.{ExecutionContextExecutor, Future}

object WebClient extends LazyLogging {

  def apply(config: ClientConfig)(implicit actorSystem: ActorSystem, materializer: ActorMaterializer,
    executionContextExecutor: ExecutionContextExecutor): WebClient =
  {
    if (config.host == "localhost") {
      logger.info("Building trustful context for local connections")
      val noCertificateCheckContext = ConnectionContext.https(trustfulSslContext())
      val apiFlow = Http().outgoingConnectionHttps(config.host, config.port, noCertificateCheckContext)
      new WebClient(apiFlow)
    } else {
      logger.info("Building secure context")
      new WebClient(Http().outgoingConnectionHttps(config.host, config.port))
    }
  }


  private def trustfulSslContext(): SSLContext = {
    val context = SSLContext.getInstance("TLS")
    context.init(Array[KeyManager](), Array(NoCheckX509TrustManager), null)
    context
  }

  object NoCheckX509TrustManager extends X509TrustManager {
    override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {}
    override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {}
    override def getAcceptedIssuers: Array[X509Certificate] = Array[X509Certificate]()
  }
}

class WebClient(private val apiFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]])
               (implicit private val actorSystem: ActorSystem,
                 private val materializer: ActorMaterializer,
                 private val executionContextExecutor: ExecutionContextExecutor) extends LazyLogging {

  private def xkomApiManager(request: HttpRequest): Future[HttpResponse] = {
    Source.single(request).via(apiFlow).runWith(Sink.head)
  }

  def fetch(url: String): Future[String] = {
    val request = RequestBuilding.Get(url)
    xkomApiManager(request).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case other =>
          val msg = s"Failed to fetch page got status $other"
          logger.error(msg)
          Future.failed(new IOException(msg))
      }
    }
  }
}
