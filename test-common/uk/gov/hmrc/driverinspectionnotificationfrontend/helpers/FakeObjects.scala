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

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import uk.gov.hmrc.driverinspectionnotificationfrontend.models.Direction.UK_INBOUND
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.InspectionStatus.InspectionPending
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.{InspectionStatus, ReportLocations}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.{Address, GvmsReferenceData, InspectionType, Location}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.{Direction, InspectionResponse}

import java.time.LocalDate

trait FakeObjects {

  def inspectionResponse(
    direction:         Direction = UK_INBOUND,
    inspectionStatus:  InspectionStatus = InspectionPending,
    reportToLocations: Option[List[ReportLocations]] = None
  ) =
    InspectionResponse(
      direction = direction,
      inspectionStatus = inspectionStatus,
      reportToLocations = reportToLocations
    )

  implicit val gvmsReferenceData: GvmsReferenceData =
    GvmsReferenceData(
      locations = Some(
        List(
          Location(
            locationId = "1",
            locationDescription = "Belfast Location 1",
            address = Address(
              lines = List(
                "1 Shamrock Lane",
                "Waldo"
              ),
              town = Some("Belfast"),
              postcode = "NI1 6JG"
            ),
            locationType = "BCP",
            locationEffectiveFrom = LocalDate.parse("2020-01-01"),
            locationEffectiveTo = None,
            supportedInspectionTypeIds = List(
              "1"
            ),
            requiredInspectionLocations = List(
              5
            )
          )
        )
      ),
      inspectionTypes = Some(
        List(
          InspectionType(
            "1",
            "CUSTOMS"
          ),
          InspectionType(
            "2",
            "DEFRA"
          ),
          InspectionType(
            "3",
            "TRANSIT"
          ),
          InspectionType(
            "4",
            "OGD"
          )
        )
      )
    )
}
