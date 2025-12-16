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

package uk.gov.hmrc.driverinspectionnotificationfrontend.views.inspectionStatusResults.cleared

import uk.gov.hmrc.driverinspectionnotificationfrontend.views.ViewBehaviours

class InspectionNotNeededGbToNiViewSpec extends ViewBehaviours {

  "/search results inspection not needed GB to NI page" should {

    val view = inspectionNotNeededGbToNiView(None)
    val doc  = view.asDocument

    behave like normalPage("no_inspection_needed_page_gb_to_ni.title", section = None)(view)
    behave like pageWithBackLink(view)
    behave like pageTechSupportLink(view)
    behave like pageFeedbackLink(view)

    "be rendered" should {

      "and show content as expected" in {

        val view = inspectionNotNeededGbToNiView(None)
        val doc  = view.asDocument

        val firstP = doc.getElementsByTag("p").first()
        firstP.text() should be("Check if this movement needs an inspection 10 minutes before departing the ferry.")

        val firstH2 = doc.getElementsByTag("h2").first()
        firstH2.text() should be("What happens next")
        firstH2
          .nextElementSiblings()
          .first()
          .text() should be(
          "You do not currently need to report for an inspection of your goods. You may still need to report for other controls when travelling to Northern Ireland."
        )

        val inspectionHeading1 = doc.getElementsByTag("h3").first()
        inspectionHeading1.text()                      should be("If you are ending a transit movement")
        inspectionHeading1.nextElementSibling().text() should be("You must also present the goods at your nearest office of destination.")

        val inspectionHeading2 = doc.getElementsByTag("h3").get(1)
        inspectionHeading2.text()                      should be("If you have no other controls to report for")
        inspectionHeading2.nextElementSibling().text() should be("You can continue from your border location of arrival, to your destination.")

        val searchLink = doc.getElementsByAttributeValue("href", "/search").first()
        searchLink.text() should be("Check another GMR")
      }
    }
  }
}
