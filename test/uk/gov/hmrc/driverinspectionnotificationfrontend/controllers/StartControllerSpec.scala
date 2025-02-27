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
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.start_page

class StartControllerSpec extends BaseSpec {

  trait SetUp {
    val startPage: start_page = new start_page(fullWidthTemplate, govukButton, hmrcPageHeading, hmrcNewTabLink)
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
      content should include("If the movement needs an inspection")
      content should include("your inspection status is not ready yet")
      content should include("<span class=\"govuk-visually-hidden\">Newid yr iaith ir Gymraeg</span>")
    }

  }
}
