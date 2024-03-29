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

import play.api.data.Form
import play.api.data.Forms.{single, text}

import java.util.Locale
object GmrSearchForm extends CustomFormats {
  private val gmrRegex = """^GMR[A-Z][0-9A-Z]{8}$"""
  val field            = "gmrId"
  val gmrSearchForm: Form[String] = Form(
    single(
      field -> text
        .transform[String](_.filterNot(_.isWhitespace).toUpperCase(Locale.UK), identity)
        .verifying("search_page.error.gmr_blank", _.nonEmpty)
        .verifying("search_page.error.gmr.format", value => value.matches(gmrRegex) || value.isEmpty)
    )
  )
}
