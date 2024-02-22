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

import cats.data.EitherT
import org.mockito.ArgumentMatchers.{any, same, eq => argEq}
import org.mockito.Mockito.when
import play.api.i18n.DefaultLangs
import play.api.i18n.Lang.logger.logger
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.InspectionLocationError.{InspectionTypeNotFound, LocationNotFound}
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.ControllerBaseSpec
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.Direction.{GB_TO_NI, NI_TO_GB, UK_INBOUND, UK_OUTBOUND}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.{InspectionStatus, ReportLocations}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.{Address, InspectionType, Location}
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.inspectionStatusResults.cleared.{inspection_not_needed_export, inspection_not_needed_gb_to_ni, inspection_not_needed_import}
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.inspectionStatusResults.inspection_pending
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.inspectionStatusResults.required.partials.guidance_common
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.inspectionStatusResults.required.{inspection_required_export, inspection_required_import}
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.partials._
import uk.gov.hmrc.play.language.LanguageUtils

import java.time.LocalDate
import scala.concurrent.Future

class SearchResultControllerSpec extends ControllerBaseSpec {

  trait SetUp {
    val nearestSitesContent = new nearest_sites_content()
    val nearestSitesheader  = new nearest_sites_header()
    val guidance            = new guidance_common(govUkInsetText)
    val inspectionRequiredImport: inspection_required_import =
      new inspection_required_import(fullWidthTemplate, hmrcPageHeading, govukWarningText, nearestSitesContent, nearestSitesheader, guidance)
    val inspectionRequiredExport: inspection_required_export =
      new inspection_required_export(
        fullWidthTemplate,
        hmrcPageHeading,
        hmrcNewTabLink,
        govukWarningText,
        nearestSitesContent,
        nearestSitesheader,
        guidance)
    val noInspectionRequiredGbToNi: inspection_not_needed_gb_to_ni = new inspection_not_needed_gb_to_ni(fullWidthTemplate, hmrcPageHeading)
    val noInspectionRequiredImport: inspection_not_needed_import   = new inspection_not_needed_import(fullWidthTemplate, hmrcPageHeading)
    val noInspectionRequiredExport: inspection_not_needed_export   = new inspection_not_needed_export(fullWidthTemplate, hmrcPageHeading)

    val inspectionPending: inspection_pending =
      new inspection_pending(
        fullWidthTemplate,
        hmrcPageHeading,
        govukButton,
        new LanguageUtils(new DefaultLangs(), configuration),
        govukNotificationBanner)

    val controller =
      new SearchResultController(
        stubMessagesControllerComponents(),
        actionBuilders(),
        mockGmsService,
        mockReferenceDataService,
        inspectionRequiredImport,
        inspectionRequiredExport,
        noInspectionRequiredGbToNi,
        noInspectionRequiredImport,
        noInspectionRequiredExport,
        inspectionPending
      )(mockAppConfig, ec)
  }

  "GET /results" when {

    List(
      UK_INBOUND,
      GB_TO_NI
    ).foreach { direction =>
      s"direction is $direction" should {
        "return 200 OK with inspection_required_import when is import and inspection data found in reference data" in new SetUp {
          val gmrId = "gmrId"
          val location = Location(
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

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(EitherT.rightT[Future, GmrErrors](inspectionResponse(
              direction         = direction,
              inspectionStatus  = InspectionStatus.InspectionRequired,
              reportToLocations = Some(List(ReportLocations(inspectionTypeId = "3", locationIds = List("1"))))
            )))

          when(mockReferenceDataService.getInspectionData(argEq(List(ReportLocations(inspectionTypeId = "3", locationIds = List("1")))))(any()))
            .thenReturn(List(Right(InspectionType("3", "TRANSIT") -> List(Right(location)))))

          val result = controller.result(gmrId, false)(FakeRequest())
          val content: String = contentAsString(result)

          status(result) shouldBe 200
          content        should include("The goods you are moving require an inspection")
          content        should include("What to do next")
          content        should include("Attending an inland border facility (IBF) check")
          content        should include("If you have to attend an IBF, you can:")
          content        should include("Ending transit movements")
        }

        "return 200 OK with inspection_required_import when is import and inspection data found in reference data with inspection types DBC, EIDR, EXEMPTION, EMPTY and CUSTOMS" in new SetUp {
          val gmrId = "gmrId"
          val location = Location(
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
              "2",
              "3",
              "4",
              "5",
              "6",
              "7",
              "8",
              "13"
            ),
            requiredInspectionLocations = List(
              2, 3, 4, 5, 6, 7, 8, 13
            )
          )
          val locationForCustoms = Location(
            locationId          = "2",
            locationDescription = "Belfast Location for Customs",
            address = Address(
              lines = List(
                "2 Shamrock Lane",
                "Waldo"
              ),
              town     = Some("Belfast"),
              postcode = "NI2 6JG"
            ),
            locationType          = "BCP",
            locationEffectiveFrom = LocalDate.parse("2020-01-01"),
            locationEffectiveTo   = None,
            supportedInspectionTypeIds = List(
              "1",
              "9",
              "10",
              "11",
              "12"
            ),
            requiredInspectionLocations = List(
              1,
              9,
              10,
              11,
              12
            )
          )

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(EitherT.rightT[Future, GmrErrors](inspectionResponse(
              direction        = direction,
              inspectionStatus = InspectionStatus.InspectionRequired,
              reportToLocations = Some(
                List(
                  ReportLocations(inspectionTypeId = "1", locationIds  = List("1")),
                  ReportLocations(inspectionTypeId = "2", locationIds  = List("1")),
                  ReportLocations(inspectionTypeId = "3", locationIds  = List("1")),
                  ReportLocations(inspectionTypeId = "4", locationIds  = List("1")),
                  ReportLocations(inspectionTypeId = "5", locationIds  = List("1")),
                  ReportLocations(inspectionTypeId = "6", locationIds  = List("1")),
                  ReportLocations(inspectionTypeId = "7", locationIds  = List("1")),
                  ReportLocations(inspectionTypeId = "8", locationIds  = List("1")),
                  ReportLocations(inspectionTypeId = "9", locationIds  = List("2")),
                  ReportLocations(inspectionTypeId = "10", locationIds = List("2")),
                  ReportLocations(inspectionTypeId = "11", locationIds = List("2")),
                  ReportLocations(inspectionTypeId = "12", locationIds = List("2")),
                  ReportLocations(inspectionTypeId = "13", locationIds = List("1"))
                )
              )
            )))

          when(
            mockReferenceDataService.getInspectionData(argEq(
              List(
                ReportLocations(inspectionTypeId = "1", locationIds  = List("1")),
                ReportLocations(inspectionTypeId = "2", locationIds  = List("1")),
                ReportLocations(inspectionTypeId = "3", locationIds  = List("1")),
                ReportLocations(inspectionTypeId = "4", locationIds  = List("1")),
                ReportLocations(inspectionTypeId = "5", locationIds  = List("1")),
                ReportLocations(inspectionTypeId = "6", locationIds  = List("1")),
                ReportLocations(inspectionTypeId = "7", locationIds  = List("1")),
                ReportLocations(inspectionTypeId = "8", locationIds  = List("1")),
                ReportLocations(inspectionTypeId = "9", locationIds  = List("2")),
                ReportLocations(inspectionTypeId = "10", locationIds = List("2")),
                ReportLocations(inspectionTypeId = "11", locationIds = List("2")),
                ReportLocations(inspectionTypeId = "12", locationIds = List("2")),
                ReportLocations(inspectionTypeId = "13", locationIds = List("1"))
              )
            ))(any()))
            .thenReturn(
              List(
                Right(InspectionType("1", "CUSTOMS")      -> List(Right(locationForCustoms))),
                Right(InspectionType("2", "DEFRA")        -> List(Right(location))),
                Right(InspectionType("3", "TRANSIT")      -> List(Right(location))),
                Right(InspectionType("4", "OGD")          -> List(Right(location))),
                Right(InspectionType("5", "DEFRA_PLANTS") -> List(Right(location))),
                Right(InspectionType("6", "ATA")          -> List(Right(location))),
                Right(InspectionType("7", "SAD")          -> List(Right(location))),
                Right(InspectionType("8", "TIR")          -> List(Right(location))),
                Right(InspectionType("9", "DBC")          -> List(Right(locationForCustoms))),
                Right(InspectionType("10", "EIDR")        -> List(Right(locationForCustoms))),
                Right(InspectionType("11", "EXEMPTION")   -> List(Right(locationForCustoms))),
                Right(InspectionType("12", "EMPTY")       -> List(Right(locationForCustoms))),
                Right(InspectionType("13", "DAERA")       -> List(Right(location))),
              )
            )

          val result = controller.result(gmrId, false)(FakeRequest())
          val content: String = contentAsString(result)

          status(result) shouldBe 200
          content        should include("The goods you are moving require an inspection")
          content        should include("What to do next")
          content        should include("Attending an inland border facility (IBF) check")
          content        should include("If you have to attend an IBF, you can:")
          content        should include("Ending transit movements")

          countSubstring("For your customs inspection", content) should be(1)

          content should include("For your DEFRA inspection")
          content should include("For your transit inspection")
          content should include("For other government department inspections")
          content should include("For your ATA Carnet endorsement")
          content should include("For your Single Administrative Document (SAD) endorsement")
          content should include("For your TIR Carnet endorsement")
          content should include("For your DAERA inspection")

          content shouldNot include("For your Entry in Declarant’s Record inspection")
          content shouldNot include("For your Oral or by conduct declaration inspection")
          content shouldNot include("For your Postal declaration inspection")
          content shouldNot include("For your Empty vehicle inspection")

        }

        "return 200 OK with inspection_required_import  and some valid inspection types found alongside unrecognised types" in new SetUp {
          val gmrId = "gmrId"
          val location = Location(
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

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(EitherT.rightT[Future, GmrErrors](inspectionResponse(
              direction        = direction,
              inspectionStatus = InspectionStatus.InspectionRequired,
              reportToLocations = Some(List(
                ReportLocations(inspectionTypeId = "3", locationIds = List("1")),
                ReportLocations(inspectionTypeId = "5", locationIds = List("1")),
              ))
            )))

          when(
            mockReferenceDataService.getInspectionData(
              argEq(
                List(
                  ReportLocations(inspectionTypeId = "3", locationIds = List("1")),
                  ReportLocations(inspectionTypeId = "5", locationIds = List("1")),
                )))(any()))
            .thenReturn(
              List(
                Right(InspectionType("3", "TRANSIT") -> List(Right(location))),
                Left(InspectionTypeNotFound("5"))
              ))

          val result = controller.result(gmrId, false)(FakeRequest())
          val content: String = contentAsString(result)

          status(result) shouldBe 200
          content        should include("The goods you are moving require an inspection")
          content        should include("What to do next")
          content        should include("Attending an inland border facility (IBF) check")
          content        should include("If you have to attend an IBF, you can:")
          content        should include("Ending transit movements")
        }

        "return 200 OK with inspection_required_import and some valid locations are present on at least one inspection type, even if some locations invalid" in new SetUp {
          val gmrId = "gmrId"
          val location = Location(
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

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(EitherT.rightT[Future, GmrErrors](inspectionResponse(
              direction        = direction,
              inspectionStatus = InspectionStatus.InspectionRequired,
              reportToLocations = Some(List(
                ReportLocations(inspectionTypeId = "3", locationIds = List("1, 2")),
                ReportLocations(inspectionTypeId = "4", locationIds = List("1")),
              ))
            )))

          when(
            mockReferenceDataService.getInspectionData(
              argEq(
                List(
                  ReportLocations(inspectionTypeId = "3", locationIds = List("1, 2")),
                  ReportLocations(inspectionTypeId = "4", locationIds = List("1")),
                )))(any()))
            .thenReturn(
              List(
                Right(InspectionType("3", "TRANSIT") -> List(Right(location), Left(LocationNotFound("2")))),
                Right(InspectionType("4", "OGD")     -> List(Right(location))),
              )
            )

          val result = controller.result(gmrId, false)(FakeRequest())
          val content: String = contentAsString(result)

          status(result) shouldBe 200
          content        should include("The goods you are moving require an inspection")
          content        should include("What to do next")
          content        should include("Attending an inland border facility (IBF) check")
          content        should include("If you have to attend an IBF, you can:")
          content        should include("Ending transit movements")
          content        should include("The inspections must take place in the order shown.")
        }

        "return 200 OK with inspection_required_import but no report locations from backend" in new SetUp {
          val gmrId = "gmrId"

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(
              EitherT.rightT[Future, GmrErrors](
                inspectionResponse(
                  direction         = direction,
                  inspectionStatus  = InspectionStatus.InspectionRequired,
                  reportToLocations = None
                )))

          when(mockReferenceDataService.getInspectionData(same(Nil))(any()))
            .thenReturn(Nil)

          val result = controller.result(gmrId, false)(FakeRequest())
          status(result)          shouldBe 200
          contentAsString(result) should include("The goods you are moving require an inspection")
          contentAsString(result) should include("What to do next")
          contentAsString(result) should include("Attending an inland border facility (IBF) check")
          contentAsString(result) should include("If you have to attend an IBF, you can:")
          contentAsString(result) should include("Ending transit movements")
        }

        "return 200 OK with inspection_required_import but no valid inspection types were found in reference data" in new SetUp {
          val gmrId = "gmrId"

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(EitherT.rightT[Future, GmrErrors](inspectionResponse(
              direction         = direction,
              inspectionStatus  = InspectionStatus.InspectionRequired,
              reportToLocations = Some(List(ReportLocations(inspectionTypeId = "5", locationIds = List("1"))))
            )))

          when(mockReferenceDataService.getInspectionData(argEq(List(ReportLocations(inspectionTypeId = "5", locationIds = List("1")))))(any()))
            .thenReturn(List(Left(InspectionTypeNotFound("5"))))

          val result = controller.result(gmrId, false)(FakeRequest())
          status(result)          shouldBe 200
          contentAsString(result) should include("The goods you are moving require an inspection")
          contentAsString(result) should include("What to do next")
          contentAsString(result) should include("Attending an inland border facility (IBF) check")
          contentAsString(result) should include("If you have to attend an IBF, you can:")
          contentAsString(result) should include("Ending transit movements")
        }

        "return 200 OK with inspection_required_import but no valid locations were found for any inspection types in reference data" in new SetUp {
          val gmrId = "gmrId"

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(EitherT.rightT[Future, GmrErrors](inspectionResponse(
              direction         = direction,
              inspectionStatus  = InspectionStatus.InspectionRequired,
              reportToLocations = Some(List(ReportLocations(inspectionTypeId = "3", locationIds = List("5"))))
            )))

          when(mockReferenceDataService.getInspectionData(argEq(List(ReportLocations(inspectionTypeId = "3", locationIds = List("5")))))(any()))
            .thenReturn(List(Right(InspectionType("3", "TRANSIT") -> List(Left(LocationNotFound("5"))))))

          val result = controller.result(gmrId, false)(FakeRequest())
          status(result)          shouldBe 200
          contentAsString(result) should include("The goods you are moving require an inspection")
          contentAsString(result) should include("What to do next")
          contentAsString(result) should include("Attending an inland border facility (IBF) check")
          contentAsString(result) should include("If you have to attend an IBF, you can:")
          contentAsString(result) should include("Ending transit movements")
          contentAsString(result) should include("For your transit inspection")
          contentAsString(result) should include("Travel to your border location of arrival")
        }

        "return 200 OK for inspection-pending (first time landing on the page)" in new SetUp {
          val gmrId = "gmrId"

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(
              EitherT.rightT[Future, GmrErrors](inspectionResponse(direction = direction, inspectionStatus = InspectionStatus.InspectionPending)))

          val result = controller.result("gmrId", false)(FakeRequest())
          status(result) shouldBe 200
          contentAsString(result) shouldNot include("Important")
          contentAsString(result) shouldNot include("There is no update to the inspection status")
          contentAsString(result) should include("Your inspection status is not ready yet")
          contentAsString(result) should include(
            "Your inspection status should be ready around 10 minutes before you reach your border location of arrival. You can check again to see if it’s ready using the button below.")
        }

        "return 200 OK for inspection-pending (second time - after clicking 'Check status again' button))" in new SetUp {
          val gmrId = "gmrId"

          when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
            .thenReturn(
              EitherT.rightT[Future, GmrErrors](inspectionResponse(direction = direction, inspectionStatus = InspectionStatus.InspectionPending)))

          val result = controller.result("gmrId", checkedStatus = true)(FakeRequest())
          status(result)          shouldBe 200
          contentAsString(result) should include("Important")
          contentAsString(result) should include("There is no update to the inspection status")
          contentAsString(result) should include("Your inspection status is not ready yet")
          contentAsString(result) should include(
            "Your inspection status should be ready around 10 minutes before you reach your border location of arrival. You can check again to see if it’s ready using the button below.")
        }
      }
    }

    "direction is UK_OUTBOUND" should {
      val direction = UK_OUTBOUND
      "return 200 OK with inspection_required_export " in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(
            EitherT.rightT[Future, GmrErrors](inspectionResponse(direction = direction, inspectionStatus = InspectionStatus.InspectionRequired)))

        when(mockReferenceDataService.getInspectionData(any())(any())).thenReturn(Nil)

        val result = controller.result(gmrId, false)(FakeRequest())
        status(result)          shouldBe 200
        contentAsString(result) should include("The goods you are moving require an inspection")
        contentAsString(result) should include("What to do next")
        contentAsString(result) should include("Attending an inland border facility (IBF) check")
        contentAsString(result) should include("If you have to attend an IBF, you can:")
        contentAsString(result) should include("Check which inspection site(s) you need to attend at your border location of departure. ")
        contentAsString(result) should include("If you have to attend an IBF in the south east area, you need to report to Sevington unless exempt.")
        contentAsString(result) should include("Vehicles are exempt if:")
        contentAsString(result) should include("the vehicle exceeds the size limit")
        contentAsString(result) should include("the vehicle contains hazardous goods")
        contentAsString(result) should include("you hold a commercial agreement with another inspection site")
        contentAsString(result) should include("Find more information on")
        contentAsString(result) should include("if your vehicle could be exempt from attending Sevington.")
      }

      "return 200 OK with inspection_required_export but no report locations from backend" in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(
            EitherT.rightT[Future, GmrErrors](
              inspectionResponse(
                direction         = direction,
                inspectionStatus  = InspectionStatus.InspectionRequired,
                reportToLocations = None
              )))

        when(mockReferenceDataService.getInspectionData(same(Nil))(any()))
          .thenReturn(Nil)

        val result = controller.result(gmrId, false)(FakeRequest())
        status(result)          shouldBe 200
        contentAsString(result) should include("The goods you are moving require an inspection")
        contentAsString(result) should include("What to do next")
        contentAsString(result) should include("Attending an inland border facility (IBF) check")
        contentAsString(result) should include("If you have to attend an IBF, you can:")
        contentAsString(result) should include("Check which inspection site(s) you need to attend at your border location of departure.")
        contentAsString(result) should include("Travel to your border location of departure.")

      }

      "return 200 OK with inspection_required_export but no valid inspection types were found in reference data" in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(EitherT.rightT[Future, GmrErrors](inspectionResponse(
            direction         = direction,
            inspectionStatus  = InspectionStatus.InspectionRequired,
            reportToLocations = Some(List(ReportLocations(inspectionTypeId = "6", locationIds = List("1"))))
          )))

        when(mockReferenceDataService.getInspectionData(argEq(List(ReportLocations(inspectionTypeId = "6", locationIds = List("1")))))(any()))
          .thenReturn(List(Left(InspectionTypeNotFound("5"))))

        val result = controller.result(gmrId, false)(FakeRequest())
        status(result)          shouldBe 200
        contentAsString(result) should include("The goods you are moving require an inspection")
        contentAsString(result) should include("What to do next")
        contentAsString(result) should include("Attending an inland border facility (IBF) check")
        contentAsString(result) should include("If you have to attend an IBF, you can:")
        contentAsString(result) should include("Travel to your border location of departure.")

      }

      "return 200 OK with inspection_required_export but no valid locations were found for any inspection types in reference data" in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(EitherT.rightT[Future, GmrErrors](inspectionResponse(
            direction         = direction,
            inspectionStatus  = InspectionStatus.InspectionRequired,
            reportToLocations = Some(List(ReportLocations(inspectionTypeId = "3", locationIds = List("5"))))
          )))

        when(mockReferenceDataService.getInspectionData(argEq(List(ReportLocations(inspectionTypeId = "3", locationIds = List("5")))))(any()))
          .thenReturn(List(Right(InspectionType("3", "TRANSIT") -> List(Left(LocationNotFound("5"))))))

        val result = controller.result(gmrId, false)(FakeRequest())
        status(result)          shouldBe 200
        contentAsString(result) should include("The goods you are moving require an inspection")
        contentAsString(result) should include("What to do next")
        contentAsString(result) should include("Attending an inland border facility (IBF) check")
        contentAsString(result) should include("If you have to attend an IBF, you can:")
        contentAsString(result) should include("For your transit inspection")
        contentAsString(result) should include("Travel to your border location of departure.")
      }

      "return 200 OK for inspection-pending (first time landing on the page)" in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(
            EitherT.rightT[Future, GmrErrors](inspectionResponse(direction = direction, inspectionStatus = InspectionStatus.InspectionPending)))

        val result = controller.result("gmrId", checkedStatus = false)(FakeRequest())
        status(result) shouldBe 200
        contentAsString(result) shouldNot include("Important")
        contentAsString(result) shouldNot include("There is no update to the inspection status")
        contentAsString(result) should include("Your inspection status is not ready yet")
        contentAsString(result) should include(
          "Your inspection status should be ready around 10 minutes before you reach your border location of arrival. You can check again to see if it’s ready using the button below.")
      }

      "return 200 OK for inspection-pending (second time - after clicking 'Check status again' button))" in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(
            EitherT.rightT[Future, GmrErrors](inspectionResponse(direction = direction, inspectionStatus = InspectionStatus.InspectionPending)))

        val result = controller.result("gmrId", checkedStatus = true)(FakeRequest())
        status(result)          shouldBe 200
        contentAsString(result) should include("Important")
        contentAsString(result) should include("There is no update to the inspection status")
        contentAsString(result) should include("Your inspection status is not ready yet")
        contentAsString(result) should include(
          "Your inspection status should be ready around 10 minutes before you reach your border location of arrival. You can check again to see if it’s ready using the button below.")
      }
    }

    "direction is UK_INBOUND" should {
      val direction = UK_INBOUND
      "return 200 OK with inspection_required_import" in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(
            EitherT.rightT[Future, GmrErrors](inspectionResponse(direction = direction, inspectionStatus = InspectionStatus.InspectionRequired)))
        when(mockReferenceDataService.getInspectionData(any())(any())).thenReturn(Nil)

        val result = controller.result(gmrId, false)(FakeRequest())
        status(result)          shouldBe 200
        contentAsString(result) should include("The goods you are moving require an inspection")
        contentAsString(result) should include("What to do next")
        contentAsString(result) should include("Attending an inland border facility (IBF) check")
        contentAsString(result) should include("If you have to attend an IBF, you can:")
        contentAsString(result) should include("Ending transit movements")
        contentAsString(result) should include("If you have to attend an IBF in the south east area, you need to report to Sevington unless exempt.")
        contentAsString(result) should include("Vehicles are exempt if:")
        contentAsString(result) should include("the vehicle exceeds the size limit")
        contentAsString(result) should include("the vehicle contains hazardous goods")
        contentAsString(result) should include("you hold a commercial agreement with another inspection site")
        contentAsString(result) should include("Find more information on")
        contentAsString(result) should include("if your vehicle could be exempt from attending Sevington.")
      }
    }

    "direction is NI_TO_GB" should {
      val direction = NI_TO_GB
      "return 200 OK with inspection_required_import" in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(
            EitherT.rightT[Future, GmrErrors](inspectionResponse(direction = direction, inspectionStatus = InspectionStatus.InspectionRequired)))
        when(mockReferenceDataService.getInspectionData(any())(any())).thenReturn(Nil)

        val result = controller.result(gmrId, false)(FakeRequest())
        status(result)          shouldBe 200
        contentAsString(result) should include("The goods you are moving require an inspection")
        contentAsString(result) should include("What to do next")
        contentAsString(result) should include("Attending an inland border facility (IBF) check")
        contentAsString(result) should include("If you have to attend an IBF, you can:")
        contentAsString(result) should include("Ending transit movements")
        contentAsString(result) shouldNot include(
          "If you have to attend an IBF in the south east area, you need to report to Sevington unless exempt.")
        contentAsString(result) shouldNot include("Vehicles are exempt if:")
        contentAsString(result) shouldNot include("the vehicle exceeds the size limit")
        contentAsString(result) shouldNot include("the vehicle contains hazardous goods")
        contentAsString(result) shouldNot include("you hold a commercial agreement with another inspection site")
        contentAsString(result) shouldNot include("Find more information on")
        contentAsString(result) shouldNot include("if your vehicle could be exempt from attending Sevington.")
      }

      "return 200 OK for inspection-pending" in new SetUp {
        val gmrId = "gmrId"

        when(mockGmsService.getInspectionStatus(argEq(gmrId))(any()))
          .thenReturn(
            EitherT.rightT[Future, GmrErrors](inspectionResponse(direction = direction, inspectionStatus = InspectionStatus.InspectionPending)))

        val result = controller.result("gmrId", false)(FakeRequest())
        status(result)          shouldBe 200
        contentAsString(result) should include("Your inspection status is not ready yet")
        contentAsString(result) should include(
          "Your inspection status should be ready around 10 minutes before you reach your border location of arrival. You can check again to see if it’s ready using the button below.")
      }
    }
  }
}
