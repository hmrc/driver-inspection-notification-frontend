/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.config

import play.api.mvc._
import cats.implicits._

object Binders {
  implicit def OptionBindable[T: PathBindable]: PathBindable[Option[T]] = new PathBindable[Option[T]] {
    def bind(key: String, value: String): Either[String, Option[T]] =
      implicitly[PathBindable[T]]
        .bind(key, value)
        .fold(
          left => left.asLeft[Option[T]],
          right => Some(right).asRight[String]
        )

    def unbind(key: String, value: Option[T]): String = value map (_.toString) getOrElse ""
  }
}
