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

package uk.gov.hmrc.driverinspectionnotificationfrontend.services

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{verify, when}
import play.api.test.Helpers.await
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.InspectionResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class GmsServiceSpec extends BaseSpec {

  trait SetUp {
    val service = new GmsService(mockGmsConnector)

    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "getInspectionStatus" should {

    "proxy happy response from connector" in new SetUp {
      val gmrId = "gmrId"

      val response: InspectionResponse = inspectionResponse()

      when(mockGmsConnector.getInspectionStatus(any())(any()))
        .thenReturn(Future(Right(response)))

      val result = await(service.getInspectionStatus(gmrId).value)

      result.right.value shouldBe response

      verify(mockGmsConnector).getInspectionStatus(any())(any())
    }

    "proxy sad response from connector" in new SetUp {
      val gmrId = "gmrId"

      when(mockGmsConnector.getInspectionStatus(any())(any()))
        .thenReturn(Future(Left(GmrErrors.GmrNotFound)))

      val response = await(service.getInspectionStatus(gmrId).value)

      response.left.value shouldBe GmrErrors.GmrNotFound

      verify(mockGmsConnector).getInspectionStatus(any())(any())
    }
  }

}
