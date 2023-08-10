/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.controllers

import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.start_page

class StartControllerSpec extends BaseSpec {

  trait SetUp {
    val startPage: start_page = new start_page(fullWidthTemplate, govukButton, formWithCSRF, hmrcPageHeading, govUkInsetText, hmrcNewTabLink)
    val controller = new StartController(stubMessagesControllerComponents(), startPage)(applicationConfig)
  }

  "GET /start" should {

    val fakeRequest = FakeRequest("GET", "/")

    "return 200" in new SetUp {
      val result = controller.show()(fakeRequest)
      status(result) shouldBe 200
      val content = contentAsString(result)
      content should include("Check if you need to report for an inspection")
      content should include("Use this service to check if you need an inspection of your goods")
      content should include("If you are entering the UK:")
      content should include("If you are leaving the UK:")
      content should include("Check this towards the end of your crossing, around 10 minutes before you reach your border location of arrival.")
      content should include("Before you start")

      content should include("You will need to enter your goods movement reference (GMR).")
      content should include("You will get one of these responses:")
      content should include("inspection needed")
      content should include("no inspection needed")
      content should include("your inspection status is not ready yet")
      content should include("<span class=\"govuk-visually-hidden\">Newid yr iaith ir Gymraeg</span>")
    }

  }
}
