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

  implicit val taskEncoder: Encoder[Task] = {
    case i: ItemQuantity =>
      i.asJson
    case r: Replacement =>
      r.asJson
  }

  implicit val taskDecoder: Decoder[Task] = (c: HCursor) =>
    c.keys.map(_.toSet) match {
      case Some(k) if k("items") =>
        c.as[Replacement]
      case Some(_) =>
        c.as[ItemQuantity]
      case None =>
        Left(DecodingFailure("Unknown json ".concat(c.toString), List.empty))
    }

  implicit val replacementEncoder: Encoder[Replacement] = (r: Replacement) =>
    Json.obj("items" -> r.items.asJson)

  implicit val replacementDecoder: Decoder[Replacement] = (c: HCursor) =>
    for {
      items <- c.downField("items").as[List[ItemQuantity]]
    } yield {
      Replacement(items)
    }

  implicit val itemQuantityEncoder: Encoder[ItemQuantity] = (a: ItemQuantity) => a.quantity match {
    case Some(quantity) =>
      Json.obj("quantity" -> quantity.asJson, "name" -> a.name.asJson)
    case None =>
      Json.obj("name" -> a.name.asJson)
  }

  implicit val itemQuantityDecoder: Decoder[ItemQuantity] = (c: HCursor) => {
    for {
      name <- c.downField("name").as[ItemName]
      quantity <- c.downField("quantity").as[Option[Int]]
    } yield {
      ItemQuantity(name, quantity)
    }
  }
}
