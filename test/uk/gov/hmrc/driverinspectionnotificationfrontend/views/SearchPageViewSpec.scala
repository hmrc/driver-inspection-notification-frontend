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

package uk.gov.hmrc.driverinspectionnotificationfrontend.views

import uk.gov.hmrc.driverinspectionnotificationfrontend.models.forms.GmrSearchForm
import uk.gov.hmrc.driverinspectionnotificationfrontend.views

class SearchPageViewSpec extends ViewBehaviours {

  "/start page" should {

    val view = searchPageView(GmrSearchForm.gmrSearchForm)
    val doc  = view.asDocument

    behave like normalPage("search_page.title", section = None)(view)
    behave like pageWithBackLink(view)
    behave like pageTechSupportLink(view)

    "be rendered" should {

      "and show content as expected" in {
        doc.getElementById("gmrId-hint").text() should be(
          "You will find this on the copy of the GMR you used to check in to this crossing. It is 12 characters starting with GMR. For example, GMRA00002KW2."
        )
        doc.getElementById("gmrId").text()                             should be("")
        doc.getElementsByAttributeValue("type", "submit").first.text() should be("Continue")
      }

      "show an error state" when {

        val expectedErrors = List(
          ("GMR ID isn't supplied", "search_page.error.gmr_blank", "Enter a GMR", ""),
          ("GMR ID has an incorrect format", "search_page.error.gmr.format", "Enter a GMR in the correct format", "$%^$%^$%^$%")
        )
        expectedErrors.foreach { case (errorType, errorKey, errorMessage, value) =>
          s"when $errorType" in {

            val viewWithError = searchPageView(GmrSearchForm.gmrSearchForm.bind(Map(GmrSearchForm.field -> value)))
            val docWithError  = viewWithError.asDocument

            // Check page title includes "Error: "
            val expectedTitle = s"Error - ${messagesImpl("search_page.title")} - ${messagesImpl("service.name")} - GOV.UK"
            docWithError.title() shouldBe expectedTitle

            // Check error summary
            val errorSummary = docWithError.getElementsByClass("govuk-error-summary__list").first()
            errorSummary.text() should include(errorMessage)

            // Check input field error message
            val fieldError = docWithError.getElementById(s"${GmrSearchForm.field}-error")
            fieldError.text() should include(errorMessage)
          }
        }
      }
    }
  }
}
