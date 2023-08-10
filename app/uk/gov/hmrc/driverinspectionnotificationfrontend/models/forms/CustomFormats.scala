/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.forms

import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.Constraints
import play.api.data.{FormError, Mapping}

trait CustomFormats extends Constraints {
  private def stringFormat(errorKey: String): Formatter[String] =
    new Formatter[String] {
      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
        data.get(key).toRight(Seq(FormError(key, errorKey, Nil)))
      def unbind(key: String, value: String) = Map(key -> value)
    }
  def nonEmptySanitisedTextWithErrorOverride(key: String, sanitisingRegex: String): Mapping[String] =
    of[String](stringFormat(key))
      .transform[String](_.replaceAll(sanitisingRegex, "").toUpperCase, identity)
      .verifying(Constraints.nonEmpty(errorMessage = key))
}
