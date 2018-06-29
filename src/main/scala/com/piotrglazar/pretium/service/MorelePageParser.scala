package com.piotrglazar.pretium.service

import org.htmlcleaner.HtmlCleaner

import scala.util.Try

class MorelePageParser {

  def extractPrice(rawPage: String): Try[BigDecimal] = Try {
    val cleaner = new HtmlCleaner()
    val page = cleaner.clean(rawPage)

    val node = page.findElementByAttValue("id", "product_price_brutto", true, false)

    BigDecimal(node.getAttributeByName("content"))
  }
}
