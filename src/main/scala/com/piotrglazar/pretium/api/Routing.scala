package com.piotrglazar.pretium.api

import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.piotrglazar.pretium.service.OptimalPriceService
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

class Routing(optimalPriceService: OptimalPriceService)(implicit private val materializer: ActorMaterializer)
  extends PretiumJsonSupport with FailFastCirceSupport {

  val route: Route = toStrictEntity(FiniteDuration(1, "minute")) {
    logRequestResult("pretium", Logging.InfoLevel) {
      path("health") {
        get {
          complete(HttpEntity(ContentTypes.`application/json`, """{"status": "OK"}"""))
        }
      } ~
      path("prices") {
        post {
          entity(as[ItemQuery]) { request =>
            val result = optimalPriceService.findOptimalPrice(request.task, request.items)
            onComplete(result) {
              case Success(itemPrices) =>
                complete(ItemQueryResponse(itemPrices).asJson)
              case Failure(t) =>
                complete(t)
            }
          }
        }
      }
    }
  }
}
