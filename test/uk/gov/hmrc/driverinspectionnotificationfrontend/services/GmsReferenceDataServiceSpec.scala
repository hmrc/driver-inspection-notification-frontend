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

import org.mockito.Mockito._
import org.scalatest.EitherValues
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.InspectionLocationError.{InspectionTypeNotFound, LocationNotFound}
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.ReportLocations
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.{Address, InspectionType, Location}

import java.time.LocalDate
import scala.concurrent.Future

class GmsReferenceDataServiceSpec extends BaseSpec with EitherValues {

  trait Setup {
    val service = new GmsReferenceDataService(mockReferenceDataConnector)
  }

  "getReferenceData" should {
    "return all the reference data" in new Setup {
      when(mockReferenceDataConnector.getReferenceData).thenReturn(
        Future(gvmsReferenceData)
      )

      val result = await(service.getReferenceData)

      result shouldBe gvmsReferenceData

      verify(mockReferenceDataConnector).getReferenceData
    }
  }

  "getInspectionData" should {
    "return empty when empty supplied" in new Setup {
      service.getInspectionData(Nil) shouldBe Nil
    }

    "return InspectionTypeNotFound" when {
      "ReportLocations contain an inspection type id not present in reference data and" when {
        "it is the only inspection id" in new Setup {
          service.getInspectionData(List(ReportLocations(inspectionTypeId = "5", locationIds = List("1")))) shouldBe
            List(Left(InspectionTypeNotFound("5")))
        }

        "there are no inspection types in the reference data" in new Setup {
          service.getInspectionData(List(ReportLocations(inspectionTypeId = "3", locationIds = List("1"))))(
            gvmsReferenceData.copy(inspectionTypes = None)) shouldBe List(Left(InspectionTypeNotFound("3")))
        }
      }
    }

    val validLocation = Location(
      locationId          = "1",
      locationDescription = "Belfast Location 1",
      address = Address(
        lines = List(
          "1 Shamrock Lane",
          "Waldo"
        ),
        town     = Some("Belfast"),
        postcode = "NI1 6JG"
      ),
      locationType          = "BCP",
      locationEffectiveFrom = LocalDate.parse("2020-01-01"),
      locationEffectiveTo   = None,
      supportedInspectionTypeIds = List(
        "1"
      ),
      requiredInspectionLocations = List(
        5
      )
    )

    "return InspectionTypeNotFound alongside valid inspection types and associated locations" when {
      "only a subset of inspection types are not found" in new Setup {
        service
          .getInspectionData(
            List(
              ReportLocations(inspectionTypeId = "3", locationIds = List("1")),
              ReportLocations(inspectionTypeId = "5", locationIds = List("1"))
            )) shouldBe
          List(
            Right(InspectionType("3", "TRANSIT") -> List(Right(validLocation))),
            Left(InspectionTypeNotFound("5"))
          )
      }
    }

    "return LocationNotFound" when {
      "ReportLocations contain a location id not present in reference data and" when {
        "it is the only location id" in new Setup {
          service.getInspectionData(List(ReportLocations(inspectionTypeId = "4", locationIds = List("2")))) shouldBe
            List(
              Right(InspectionType("4", "OGD") -> List(Left(LocationNotFound("2"))))
            )
        }

        "it is alongside other location ids which are present in the same ReportLocations object" in new Setup {
          service
            .getInspectionData(
              List(
                ReportLocations(inspectionTypeId = "3", locationIds = List("1", "2"))
              )) shouldBe
            List(
              Right(InspectionType("3", "TRANSIT") -> List(Right(validLocation), Left(LocationNotFound("2"))))
            )
        }

        "it is alongside other location ids which are present in another ReportLocations object" in new Setup {
          service
            .getInspectionData(
              List(
                ReportLocations(inspectionTypeId = "3", locationIds = List("1")),
                ReportLocations(inspectionTypeId = "4", locationIds = List("2"))
              )) shouldBe
            List(
              Right(InspectionType("3", "TRANSIT") -> List(Right(validLocation))),
              Right(InspectionType("4", "OGD")     -> List(Left(LocationNotFound("2"))))
            )
        }

        "there are no locations in reference data" in new Setup {
          service
            .getInspectionData(
              List(
                ReportLocations(inspectionTypeId = "4", locationIds = List("1"))
              ))(gvmsReferenceData.copy(locations = None)) shouldBe
            List(
              Right(InspectionType("4", "OGD") -> List(Left(LocationNotFound("1"))))
            )
        }
      }
    }
  }
}
