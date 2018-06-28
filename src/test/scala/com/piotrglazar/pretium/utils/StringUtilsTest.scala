package com.piotrglazar.pretium.utils

import StringUtils.StringOps
import org.scalatest.{FlatSpec, Matchers}

class StringUtilsTest extends FlatSpec with Matchers {

  it should "remove whitespaces" in {
    // given
    val someString = "a b \u00A0 c"

    // when
    val result = someString.removeAllWhitespaces()

    // then
    result shouldEqual "abc"
  }

}
