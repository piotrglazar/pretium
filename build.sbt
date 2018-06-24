import Libraries._

lazy val root = (project in file("."))
  .settings(
    name := "pretium",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      Utils.guava,
      Utils.logback,
      Utils.config,
      Utils.logging,
      Utils.htmlCleaner,
      TestUtils.scalatest,
      TestUtils.mockito,
      TestUtils.restito
    ) ++ Core.akka ++ Core.akkaTest ++ Circe.circe,
    version := "0.1.0",
    organization := "com.piotrglazar",
    publishMavenStyle := true
  )

