package com.piotrglazar.pretium

import com.piotrglazar.pretium.api.{ClientConfig, ItemSourceName}
import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import com.typesafe.config.ConfigFactory

object Config {

  private lazy val config = ConfigFactory.load()

  lazy val port: Int = config.getInt("app.port")

  lazy val clientConfigs: Map[ItemSourceName, ClientConfig] = readClientConfigs()

  private def readClientConfigs(): Map[ItemSourceName, ClientConfig] = {
    import scala.collection.JavaConverters._
    config.getConfigList("sources")
      .asScala
      .map { c =>
        val name = ItemSourceName.withName(c.getString("name"))
        name -> new ClientConfig {
          override def host: String = c.getString("host")

          override def port: Int = c.getInt("port")
        }
      } (collection.breakOut)
  }
}
