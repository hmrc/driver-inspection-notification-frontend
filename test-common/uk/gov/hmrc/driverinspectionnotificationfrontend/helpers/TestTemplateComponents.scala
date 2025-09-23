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

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import uk.gov.hmrc.driverinspectionnotificationfrontend.views.helpers.NonStandardHmrcFooterItems
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.helpers.getHelp
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.{govukTwoThirdsLayout, head, main_layout_full_width_template}
import uk.gov.hmrc.govukfrontend.views.html.components.*
import uk.gov.hmrc.govukfrontend.views.html.helpers.{GovukFormGroup, GovukHintAndErrorMessage, GovukLogo}
import uk.gov.hmrc.hmrcfrontend.config.{AccessibilityStatementConfig, AssetsConfig, ContactFrontendConfig, RebrandConfig, TrackingConsentConfig, TudorCrownConfig}
import uk.gov.hmrc.hmrcfrontend.views.html.components.*
import uk.gov.hmrc.hmrcfrontend.views.html.helpers.{HmrcScripts, HmrcStandardHeader, HmrcTrackingConsentSnippet}

trait TestTemplateComponents {
  self: Configs =>

  val additionalAppConfig: Map[String, Any] = Map.empty

  val hmrcTrackingConsent = new HmrcTrackingConsentSnippet(new TrackingConsentConfig(configuration))
  val govukLogo           = new GovukLogo
  val govukTemplate =
    new GovukTemplate(
      new GovukHeader(TudorCrownConfig(configuration), RebrandConfig(configuration), govukLogo),
      new GovukFooter(RebrandConfig(configuration), govukLogo),
      new GovukSkipLink,
      new FixedWidthPageLayout,
      RebrandConfig(configuration)
    )

  val technicalIssueSnippet = new getHelp(new HmrcReportTechnicalIssue(), new ContactFrontendConfig(configuration))

  val accessibilityConfiguration = new AccessibilityStatementConfig(configuration)

  val nonStandardHmrcFooterItems = new NonStandardHmrcFooterItems(accessibilityConfiguration)

  val hmrcStandardHeader = new HmrcStandardHeader(
    hmrcHeader = new HmrcHeader(
      hmrcBanner = new HmrcBanner(TudorCrownConfig(configuration)),
      hmrcUserResearchBanner = new HmrcUserResearchBanner(),
      govukPhaseBanner = new GovukPhaseBanner(govukTag = new GovukTag()),
      tudorCrownConfig = TudorCrownConfig(configuration),
      rebrandConfig = RebrandConfig(configuration),
      govukLogo = govukLogo,
      govukServiceNavigation = GovukServiceNavigation()
    )
  )

  val assetsConfig = new AssetsConfig()

  val hmrcScripts = new HmrcScripts(assetsConfig)

  val govUkFooter = new GovukFooter(
    rebrandConfig = RebrandConfig(configuration),
    govukLogo = govukLogo
  )
  val fullWidthTemplate =
    new main_layout_full_width_template(
      new govukTwoThirdsLayout(
        govukTemplate,
        hmrcStandardHeader,
        new HmrcFooter(govUkFooter),
        technicalIssueSnippet,
        hmrcScripts,
        nonStandardHmrcFooterItems
      ),
      new head(hmrcTrackingConsent),
      new GovukBackLink
    )

  val govukHintAndErrorMessage = new GovukHintAndErrorMessage(new GovukHint, new GovukErrorMessage)
  val govukFormGroup           = new GovukFormGroup
  val govukLabel               = new GovukLabel

  val govukFieldSet           = new GovukFieldset
  val govukRadios             = new GovukRadios(govukFieldSet, new GovukHint, govukLabel, govukFormGroup, govukHintAndErrorMessage)
  val govukInput              = new GovukInput(govukLabel, govukFormGroup, govukHintAndErrorMessage)
  val govukDateInput          = new GovukDateInput(govukFieldSet, govukInput, govukFormGroup, govukHintAndErrorMessage)
  val govukDetails            = new GovukDetails
  val govukPanel              = new GovukPanel
  val govukTable              = new GovukTable
  val govukButton             = new GovukButton
  val govukErrorSummary       = new GovukErrorSummary
  val govukSummaryList        = new GovukSummaryList
  val govukSelect             = new GovukSelect(govukLabel, govukFormGroup, govukHintAndErrorMessage)
  val govukBackLink           = new GovukBackLink
  val govukWarningText        = new GovukWarningText
  val formWithCSRF            = new FormWithCSRF
  val hmrcPageHeading         = new HmrcPageHeading
  val govUkInsetText          = new GovukInsetText
  val hmrcNewTabLink          = new HmrcNewTabLink
  val govukNotificationBanner = new GovukNotificationBanner

}
