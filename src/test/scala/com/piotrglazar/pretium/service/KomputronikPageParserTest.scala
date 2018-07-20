package com.piotrglazar.pretium.service

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class KomputronikPageParserTest extends FlatSpec with Matchers {

  private val parser = new KomputronikPageParser

  private lazy val page = Source.fromFile(Resources.getResource("intel-i7-komputronik.html").toURI, Charsets.UTF_8.displayName()).mkString

  private lazy val gpuPage = Source.fromFile(Resources.getResource("nvidia-1080ti-komputronik.html").toURI, Charsets.UTF_8.displayName()).mkString

  it should "extract price" in {
    // given 'page'

    // when
    val result = parser.extractPrice(page)

    // then
    result should be a 'success
    result.get shouldEqual BigDecimal("1574.90")
  }

  it should "extract gpu price and not take 'other products' into consideration" in {
    // given 'gpuPage'

    // when
    val result = parser.extractPrice(gpuPage)

    // then
    result should be a 'success
    result.get shouldEqual BigDecimal("4269.90")
  }
}
