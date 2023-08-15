/*
 * Copyright 2023 HM Revenue & Customs
 *
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
