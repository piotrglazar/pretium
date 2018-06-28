package com.piotrglazar.pretium.service

import com.piotrglazar.pretium.utils.StringUtils.StringOps

import com.typesafe.scalalogging.LazyLogging
import org.htmlcleaner.HtmlCleaner

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class KomputronikPageParser extends LazyLogging {

  def extractPrice(rawPage: String): Try[BigDecimal] = {
    val cleaner = new HtmlCleaner()
    val page = cleaner.clean(rawPage)

    val node = page.getElementListByAttValue("class", "proper", true, false).asScala

    val prices = for {
      n <- node
      nodeText = n.getText.toString.removeAllWhitespaces().replace("zł", "")
      cents = Option(n.findElementByAttValue("class", "cent", false, false)).map(_.getText.toString)
    } yield parsePrice(nodeText, cents)


    if (prices.nonEmpty) {
      Success(prices.min)
    } else {
      Failure(new IllegalStateException("Unable to parse price"))
    }
  }

  private def parsePrice(fullText: String, cents: Option[String]): BigDecimal = {
    cents match {
      case Some(centsValue) =>
        val value = fullText.dropRight(centsValue.length)
        BigDecimal(s"$value.$centsValue")
      case None =>
        BigDecimal(fullText)
    }
  }
}
