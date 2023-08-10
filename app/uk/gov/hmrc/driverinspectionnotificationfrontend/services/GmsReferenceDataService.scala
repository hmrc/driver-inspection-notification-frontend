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

import uk.gov.hmrc.driverinspectionnotificationfrontend.connectors.GoodsMovementSystemReferenceDataConnector
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.InspectionLocationError._
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.ReportLocations
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.{GvmsReferenceData, InspectionType, Location}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class GmsReferenceDataService @Inject()(
  goodsMovementSystemReferenceDataConnector: GoodsMovementSystemReferenceDataConnector
) {

  def getReferenceData(implicit hc: HeaderCarrier): Future[GvmsReferenceData] =
    goodsMovementSystemReferenceDataConnector.getReferenceData

  def getInspectionData(reportToLocations: List[ReportLocations])(
    implicit referenceData:                GvmsReferenceData): List[Either[InspectionTypeNotFound, (InspectionType, List[Either[LocationNotFound, Location]])]] =
    reportToLocations.map { reportToLocation =>
      {
        referenceData.inspectionTypes
          .getOrElse(Nil)
          .find(_.inspectionTypeId == reportToLocation.inspectionTypeId)
          .toRight(InspectionTypeNotFound(reportToLocation.inspectionTypeId))
          .map { inspectionType =>
            (inspectionType, getLocations(reportToLocation.locationIds, referenceData.locations.getOrElse(Nil)))
          }
      }
    }

  private def getLocations(locationIds: List[String], refLocations: List[Location]): List[Either[LocationNotFound, Location]] =
    locationIds.map { locationId =>
      refLocations.find(_.locationId == locationId).toRight(LocationNotFound(locationId))
    }
}
