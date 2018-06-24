package com.piotrglazar.pretium.service

import org.htmlcleaner.HtmlCleaner

import scala.collection.JavaConverters._
import scala.util.Try

class XkomPageParser {

  def extractPrice(rawPage: String): Try[BigDecimal] = Try {
    val cleaner = new HtmlCleaner()
    val page = cleaner.clean(rawPage)

    val nodes = page.getElementListByAttValue("itemprop", "price", true, false).asScala

    (for {
      n <- nodes
      if n.hasAttribute("content")
      rawContent = n.getAttributeByName("content")
      price = BigDecimal(rawContent)
    } yield price).head
  }
}
