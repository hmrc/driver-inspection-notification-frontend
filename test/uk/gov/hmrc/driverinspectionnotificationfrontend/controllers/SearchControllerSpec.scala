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

package uk.gov.hmrc.driverinspectionnotificationfrontend.controllers

import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.search_page

class SearchControllerSpec extends BaseSpec {

  trait SetUp {
    val searchPage: search_page = new search_page(fullWidthTemplate, govukButton, formWithCSRF, govukErrorSummary, hmrcPageHeading, govukInput)

    val controller = new SearchController(
      stubMessagesControllerComponents(),
      searchPage
    )
  }

  "GET /search" should {

    val fakeRequest = FakeRequest("GET", "/")

    "return 200" in new SetUp {
      val result = controller.show(None)(fakeRequest)
      contentAsString(result) should include("Check if you need to report for an inspection")
      contentAsString(result) should include(
        "You will find this on the copy of the GMR you used to check in to this crossing. It is 12 characters starting with GMR. For example, GMRA00002KW2."
      )
      contentAsString(result) should include("What is your goods movement reference (GMR)?")
      contentAsString(result) should include("Continue")
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
    }

    "return 400" in new SetUp {
      val result = controller.show(None)(fakeRequest.withFlash(("not-found", "1234")))
      contentAsString(result) should include("Check if you need to report for an inspection")
      contentAsString(result) should include(
        "You will find this on the copy of the GMR you used to check in to this crossing. It is 12 characters starting with GMR. For example, GMRA00002KW2."
      )
      contentAsString(result) should include("What is your goods movement reference (GMR)?")
      contentAsString(result) should include("Continue")
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
    }
  }

  "POST /search" should {
    val validGmr                  = "GMRA00002KW2"
    val fakeRequestWithValidGmr   = FakeRequest(routes.SearchController.submit()).withBody(Map("gmrId" -> List(validGmr)))
    val invalidGmr                = "<>!"
    val fakeRequestWithInvalidGmr = FakeRequest(routes.SearchController.submit()).withBody(Map("gmrId" -> List(invalidGmr)))

    "Redirect to results page" in new SetUp {

      val result = controller.submit()(fakeRequestWithValidGmr)
      redirectLocation(result) shouldBe Some(routes.SearchResultController.result(validGmr, checkedStatusAgain = false).url)
    }

    "Bad Request: Invalid gmr" in new SetUp {
      val result = controller.submit()(fakeRequestWithInvalidGmr)
      status(result)        shouldBe 400
      contentAsString(result) should include("Enter a GMR in the correct format")
    }

  }
}
