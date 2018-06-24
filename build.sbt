import Libraries.{Util, Core, TestUtils}

lazy val root = (project in file("."))
  .settings(
    name := "pretium",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      Util.guava,
      Util.logback,
      Util.config,
      TestUtils.scalatest
    ) ++ Core.akka ++ Core.akkaTest,
    version := "0.1.0",
    organization := "com.piotrglazar",
    publishMavenStyle := true
  )

