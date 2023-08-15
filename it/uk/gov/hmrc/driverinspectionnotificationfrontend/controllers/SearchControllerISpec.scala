/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.controllers

import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.{BaseISpec, WireMockConfig, WireMockHelper, WireMockSupport}

import scala.concurrent.Future

class SearchControllerISpec extends BaseISpec with WireMockSupport with WireMockHelper with WireMockConfig {

  "GET /start" should {
    "return 200 and HTML for the page" in {

      val result =
        callRoute(FakeRequest(routes.SearchController.show()))

      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      val content = contentAsString(result)
      content         should include("What is your goods movement reference (GMR)?")
      charset(result) shouldBe Some("utf-8")
    }

    "return 400 BAD_REQUEST and HTML for the page" in {

      val result =
        callRoute(FakeRequest(routes.SearchController.show()).withFlash(("not-found", "GMRA00002KW2")))

      status(result)      shouldBe BAD_REQUEST
      contentType(result) shouldBe Some("text/html")
      val content = contentAsString(result)
      content         should include("Error - What is your goods movement reference (GMR)?")
      content         should include("There is a problem")
      content         should include("GMR not found. You will need to check the details of the GMR and enter it again.")
      content         should include("GMRA00002KW2")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "POST /search" should {

    "return 303 and redirect to results page" in {

      val result: Future[Result] = callRoute(FakeRequest(routes.SearchController.submit()).withFormUrlEncodedBody("gmrId" -> "GMRA00002KW2"))

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SearchResultController.result("GMRA00002KW2").url)
    }
  }

}
