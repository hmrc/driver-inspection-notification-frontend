/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.{InspectionStatus, ReportLocations}

case class InspectionResponse(
  direction:         Direction,
  inspectionStatus:  InspectionStatus,
  reportToLocations: Option[List[ReportLocations]]
)

object InspectionResponse {
  implicit val format: OFormat[InspectionResponse] = Json.format

}
