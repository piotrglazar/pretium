import sbt._

object Libraries {

  object Core {
    private val akkaVersion = "2.5.13"
    private val akkaStreamsVersion = "10.1.3"

    lazy val akka: Seq[ModuleID] = Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaStreamsVersion
    )

    lazy val akkaTest: Seq[ModuleID] = Seq(
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaStreamsVersion % Test
    )
  }

  object Utils {
    lazy val guava: ModuleID = "com.google.guava" % "guava" % "25.0-jre"
    lazy val logback: ModuleID = "ch.qos.logback" % "logback-classic" % "1.3.0-alpha4"
    lazy val config: ModuleID = "com.typesafe" % "config" % "1.3.1"
    lazy val logging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
    lazy val htmlCleaner: ModuleID = "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.22"
  }

  object TestUtils {
    lazy val scalatest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5" % Test
    lazy val mockito: ModuleID = "org.mockito" % "mockito-core" % "2.19.0" % Test
    lazy val restito: ModuleID = "com.xebialabs.restito" % "restito" % "0.9.3" % Test
  }

  object Circe {
    private val circeVersion = "0.9.3"

    private lazy val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % "1.21.0"

    lazy val circe: Seq[ModuleID] = Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      akkaHttpCirce
    )
  }
}
