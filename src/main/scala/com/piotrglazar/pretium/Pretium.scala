package com.piotrglazar.pretium

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Pretium extends App {

  private val port = 8080

  private implicit val system: ActorSystem = ActorSystem("pretium")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  private val helloRoute: Route = path("hello") {
    get {
      complete(HttpEntity(ContentTypes.`application/json`, """{"message": "Hello world!"}"""))
    }
  }

  private val bindingFuture = Http().bindAndHandle(helloRoute, "localhost", port)

  println(s"Server is running on port $port")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
