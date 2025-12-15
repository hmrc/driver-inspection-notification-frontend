/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.driverinspectionnotificationfrontend.views.inspectionStatusResults

import org.jsoup.nodes.Document
import play.api.i18n.DefaultLangs
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.Direction.UK_INBOUND
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.{Address, Location}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.views.InspectionDisplayGroup
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.views.InspectionDisplayGroup.{CUSTOMS, DEFRA_PLANTS, DEFRA_TRANSIT}
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.ViewBehaviours
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.inspectionStatusResults.inspection_pending
import uk.gov.hmrc.play.language.LanguageUtils

import java.time.LocalDate

class InspectionPendingViewSpec extends ViewBehaviours {

  val languageUtils = new LanguageUtils(new DefaultLangs(), configuration)
  val inspectionPendingView = new inspection_pending(
    fullWidthTemplate,
    govukButton,
    languageUtils,
    govukNotificationBanner
  )

  "/search results inspection pending page" should {

    val gmrId = "GMRB00QT08MS"
    val view  = inspectionPendingView(gmrId, checkedStatusAgain = false)
    val doc   = view.asDocument

    behave like normalPage("inspection_pending_page.title", section = None)(view)
    behave like pageWithBackLink(view)
    behave like pageTechSupportLink(view)
    behave like pageFeedbackLink(view)

    "be rendered" should {

      "and show content as expected" in {

        val view = inspectionPendingView(gmrId, checkedStatusAgain = false)
        val doc  = view.asDocument

        val bannerTitle = doc.getElementsByClass("govuk-notification-banner__title")
        bannerTitle shouldBe empty

        val bannerContent = doc.getElementsByClass("govuk-notification-banner__heading")
        bannerContent shouldBe empty

        val elements = doc.getElementsByTag("h1").first().nextElementSiblings()
        assert(elements.get(0).text().contains("Last checked at"))
        elements.get(1).text() should be(
          "Your inspection status should be ready around 10 minutes before you reach your border location of arrival. You can check again to see if it’s ready using the button below."
        )
        elements.get(2).nextElementSibling().text()       should be("Check status again")
        elements.get(2).nextElementSibling().attr("href") should be(s"/results/$gmrId?checkedStatusAgain=true")

        val searchLink = doc.getElementsByAttributeValue("href", "/search").first()
        searchLink.text() should be("Check another GMR")
      }

      "and show content as expected with checkedStatusAgain set to true" in {

        val view = inspectionPendingView(gmrId, checkedStatusAgain = true)
        val doc  = view.asDocument

        val bannerTitle = doc.getElementsByClass("govuk-notification-banner__title").first()
        bannerTitle.text() should be("Important")

        val bannerContent = doc.getElementsByClass("govuk-notification-banner__heading").first()
        bannerContent.text() should be("There is no update to the inspection status")

        val elements = doc.getElementsByTag("h1").first().nextElementSiblings()
        assert(elements.get(0).text().contains("Last checked at"))
        elements.get(1).text() should be(
          "Your inspection status should be ready around 10 minutes before you reach your border location of arrival. You can check again to see if it’s ready using the button below."
        )
        elements.get(2).nextElementSibling().text()       should be("Check status again")
        elements.get(2).nextElementSibling().attr("href") should be(s"/results/$gmrId?checkedStatusAgain=true")

        val searchLink = doc.getElementsByAttributeValue("href", "/search").first()
        searchLink.text() should be("Check another GMR")
      }
    }
  }
}
