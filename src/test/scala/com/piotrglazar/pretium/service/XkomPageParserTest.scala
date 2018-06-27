package com.piotrglazar.pretium.service

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class XkomPageParserTest extends FlatSpec with Matchers {

  private val parser = new XkomPageParser

  private lazy val page = Source.fromFile(Resources.getResource("intel-i7-xkom.html").toURI, Charsets.UTF_8.displayName()).mkString

  it should "extract product price" in {
    // given 'page'

    // when
    val result = parser.extractPrice(page)

    // then
    result should be a 'success
    result.get shouldEqual BigDecimal(1559)
  }
}
