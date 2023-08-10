/*
 * Copyright 2023 HM Revenue & Customs
 *
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
