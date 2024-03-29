/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.forms

import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.Constraints
import play.api.data.{FormError, Mapping}

import java.util.Locale

trait CustomFormats extends Constraints {
  private def stringFormat(errorKey: String): Formatter[String] =
    new Formatter[String] {
      def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
        data.get(key).toRight(Seq(FormError(key, errorKey, Nil)))
      def unbind(key: String, value: String) = Map(key -> value)
    }
  def nonEmptySanitisedTextWithErrorOverride(key: String, sanitisingRegex: String): Mapping[String] =
    of[String](stringFormat(key))
      .transform[String](_.replaceAll(sanitisingRegex, "").toUpperCase(Locale.UK), identity)
      .verifying(Constraints.nonEmpty(errorMessage = key))
}
