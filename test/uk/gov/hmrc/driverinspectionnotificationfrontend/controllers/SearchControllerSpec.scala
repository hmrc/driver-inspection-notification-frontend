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
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.search

class SearchControllerSpec extends BaseSpec {

  trait SetUp {
    val searchView: search = new search(fullWidthTemplate, govukButton, formWithCSRF, govukErrorSummary, hmrcPageHeading, govukInput)

    val controller = new SearchController(
      stubMessagesControllerComponents(),
      searchView
    )
  }

  "GET /search" should {

    val fakeRequest = FakeRequest("GET", "/")

    "return 200 OK" in new SetUp {
      val result = controller.show(None)(fakeRequest)
      status(result)        shouldBe OK
      contentAsString(result) should include("Check if you need to report for an inspection")
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
    }

    "return 400 BAD_REQUEST" in new SetUp {
      val result = controller.show(None)(fakeRequest.withFlash(("not-found", "1234")))
      status(result)        shouldBe BAD_REQUEST
      contentAsString(result) should include("Check if you need to report for an inspection")
      contentAsString(result) should include(
        "GMR not found. You will need to check the details of the GMR and enter it again."
      )
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "POST /search" should {
    val validGmr                  = "GMRA00002KW2"
    val fakeRequestWithValidGmr   = FakeRequest(routes.SearchController.submit()).withBody(Map("gmrId" -> List(validGmr)))
    val invalidGmr                = "<>!"
    val fakeRequestWithInvalidGmr = FakeRequest(routes.SearchController.submit()).withBody(Map("gmrId" -> List(invalidGmr)))

    "redirect to results page" in new SetUp {
      val result = controller.submit()(fakeRequestWithValidGmr)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SearchResultController.result(validGmr, checkedStatusAgain = false).url)
    }

    "return 400 BAD REQUEST: Invalid gmr" in new SetUp {
      val result = controller.submit()(fakeRequestWithInvalidGmr)
      status(result) shouldBe BAD_REQUEST
    }

  }
}
