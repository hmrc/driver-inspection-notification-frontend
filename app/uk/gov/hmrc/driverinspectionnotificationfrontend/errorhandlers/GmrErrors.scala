/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers

sealed trait GmrErrors

object GmrErrors {
  case object GmrNotFound extends GmrErrors

  val gmrNotFound: GmrErrors = GmrNotFound
}

sealed trait InspectionLocationError

object InspectionLocationError {

  final case class InspectionTypeNotFound(inspectionTypeId: String) extends InspectionLocationError
  final case class LocationNotFound(locationId:             String) extends InspectionLocationError

}
