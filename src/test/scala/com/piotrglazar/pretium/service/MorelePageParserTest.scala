package com.piotrglazar.pretium.service

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class MorelePageParserTest extends FlatSpec with Matchers {

  private val parser = new MorelePageParser

  private lazy val page = Source.fromFile(Resources.getResource("intel-i7-morele.html").toURI, Charsets.UTF_8.displayName()).mkString

  it should "extract product price" in {
    // given 'page'

    // when
    val result = parser.extractPrice(page)

    // then
    result should be a 'success
    result.get shouldBe BigDecimal("1569")
  }
}
