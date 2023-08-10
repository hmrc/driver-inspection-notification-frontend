/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.forms

import play.api.data.Form
import play.api.data.Forms.{single, text}
object GmrSearchForm extends CustomFormats {
  private val gmrRegex = """^GMR[A-Z][0-9A-Z]{8}$"""
  val field            = "gmrId"
  val gmrSearchForm: Form[String] = Form(
    single(
      field -> text
        .transform[String](_.filterNot(_.isWhitespace).toUpperCase(), identity)
        .verifying("search_page.error.gmr_blank", _.nonEmpty)
        .verifying("search_page.error.gmr.format", value => value.matches(gmrRegex) || value.isEmpty)
    )
  )
}
