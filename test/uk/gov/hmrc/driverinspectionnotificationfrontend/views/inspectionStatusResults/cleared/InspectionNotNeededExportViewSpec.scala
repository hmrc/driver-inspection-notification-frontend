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

class InspectionNotNeededExportViewSpec extends ViewBehaviours {

  "/search results inspection not needed export page" should {

    val view = inspectionNotNeededExportView(None)
    val doc  = view.asDocument

    behave like normalPage("no_inspection_needed_page_export.title", section = None)(view)
    behave like pageWithBackLink(view)
    behave like pageTechSupportLink(view)
    behave like pageFeedbackLink(view)

    "be rendered" should {

      "and show content as expected" in {

        val view = inspectionNotNeededExportView(None)
        val doc  = view.asDocument

        val firstH2 = doc.getElementsByTag("h2").first()
        firstH2.text() should be("What happens next")
        firstH2
          .nextElementSiblings()
          .first()
          .text() should be("You do not need to report for an inspection of your goods, but you may still need to report for other controls.")

        val inspectionHeading1 = doc.getElementsByTag("h3").first()
        inspectionHeading1.text() should be("Other additional controls")
        inspectionHeading1.nextElementSibling().text() should be(
          "Some movements may need additional controls, for example food, animals or organics. Check with your declarant."
        )

        val inspectionHeading2 = doc.getElementsByTag("h3").get(1)
        inspectionHeading2.text()                      should be("If you have no other controls to report for")
        inspectionHeading2.nextElementSibling().text() should be("You can continue to your border location of departure.")

        val searchLink = doc.getElementsByAttributeValue("href", "/search").first()
        searchLink.text() should be("Check another GMR")
      }
    }
  }
}
