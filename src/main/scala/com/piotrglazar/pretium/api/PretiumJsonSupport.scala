package com.piotrglazar.pretium.api

import com.piotrglazar.pretium.api.ItemSourceName.ItemSourceName
import io.circe._
import io.circe.syntax._

import scala.util.{Failure, Success, Try}

trait PretiumJsonSupport {

  implicit val itemSourceDecoder: Decoder[ItemSourceName] = (c: HCursor) => c.as[String].flatMap(rawString =>
    Try(ItemSourceName.withName(rawString)) match {
      case Success(itemSourceValue) => Right(itemSourceValue)
      case Failure(_) => Left(DecodingFailure("Unknown value ".concat(rawString), List.empty))
    }
  )

  implicit val itemSourceEncoder: Encoder[ItemSourceName] = (a: ItemSourceName) => a.toString.asJson

  implicit val itemNameDecoder: Decoder[ItemName] = (c: HCursor) => c.as[String].map(ItemName)

  implicit val itemNameEncoder: Encoder[ItemName] = (a: ItemName) => a.value.asJson
}
