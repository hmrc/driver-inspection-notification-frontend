/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata

import play.api.libs.json.{Format, JsResult, JsValue, Json, OFormat, Writes}

import java.time.LocalDate

case class Location(
  locationId:                  String,
  locationDescription:         String,
  locationType:                String,
  address:                     Address,
  locationEffectiveFrom:       LocalDate,
  locationEffectiveTo:         Option[LocalDate],
  supportedInspectionTypeIds:  List[String],
  requiredInspectionLocations: List[Int]
)

object Location {
  implicit val format: OFormat[Location] = Json.format[Location]
}

case class Address(
  lines:                List[String],
  town:                 Option[String],
  private val postcode: String
) {
  def getPostcode = if (postcode.equalsIgnoreCase("n/a")) "" else postcode
}

object Address {
  implicit val format: OFormat[Address] = Json.format[Address]
}
