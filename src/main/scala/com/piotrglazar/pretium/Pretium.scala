package com.piotrglazar.pretium

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.piotrglazar.pretium.api.{ItemSourceName, Routing}
import com.piotrglazar.pretium.api.clients.WebClient
import com.piotrglazar.pretium.service._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Pretium extends App with LazyLogging {

  private implicit val system: ActorSystem = ActorSystem("pretium")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  private val xkomService = new XkomService(WebClient(Config.clientConfigs(ItemSourceName.XKOM)), new XkomPageParser)

  private val komputronikService = new KomputronikService(WebClient(Config.clientConfigs(ItemSourceName.KOMPUTRONIK)),
    new KomputronikPageParser)

  private val moreleService = new MoreleService(WebClient(Config.clientConfigs(ItemSourceName.MORELE)),
    new MorelePageParser)

  private val optimalPriceService: OptimalPriceService = new OptimalPriceService(
    List(xkomService, komputronikService, moreleService)
  )

  private val routing: Routing = new Routing(optimalPriceService)

  private val bindingFuture = Http().bindAndHandle(routing.route, "localhost", Config.port)

  logger.info(s"Server is running on port ${Config.port}")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
