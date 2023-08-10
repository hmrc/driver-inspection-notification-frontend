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
