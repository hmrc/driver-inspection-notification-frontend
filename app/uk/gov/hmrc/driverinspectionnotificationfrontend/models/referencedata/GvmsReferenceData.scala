/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata

import play.api.libs.json.{Json, OFormat}

case class GvmsReferenceData(
  locations:       Option[List[Location]],
  inspectionTypes: Option[List[InspectionType]]
)

object GvmsReferenceData {
  implicit val format: OFormat[GvmsReferenceData] = Json.format[GvmsReferenceData]
}
