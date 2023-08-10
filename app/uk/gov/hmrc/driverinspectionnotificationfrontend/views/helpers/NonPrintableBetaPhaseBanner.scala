/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.views.helpers

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.phasebanner.PhaseBanner
import uk.gov.hmrc.govukfrontend.views.viewmodels.tag.Tag
import uk.gov.hmrc.hmrcfrontend.views.config.StandardPhaseBanner

class NonPrintableBetaPhaseBanner extends StandardPhaseBanner {

  override def apply(phase: String, url: String)(implicit messages: Messages): Aliases.PhaseBanner =
    PhaseBanner(
      tag     = Some(Tag(content = Text(phase))),
      classes = "govuk-!-display-none-print",
      content = HtmlContent(
        s"""${messages("phase.banner.before")} <a class=\"govuk-link\" href=\"${HtmlFormat
          .escape(url)}\">${messages("phase.banner.link")}</a> ${messages("phase.banner.after")}"""
      )
    )

  def apply(url: String)(implicit messages: Messages): PhaseBanner =
    apply(phase = "beta", url = url)

}
