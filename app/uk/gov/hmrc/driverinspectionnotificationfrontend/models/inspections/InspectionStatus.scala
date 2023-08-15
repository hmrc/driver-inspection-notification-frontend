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

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections

import play.api.libs.json._

sealed trait InspectionStatus

object InspectionStatus {

  case object InspectionRequired extends InspectionStatus
  case object InspectionNotNeeded extends InspectionStatus
  case object InspectionPending extends InspectionStatus

  implicit val format: Format[InspectionStatus] = new Format[InspectionStatus] {
    override def reads(json: JsValue): JsResult[InspectionStatus] =
      json.as[String] match {
        case "REQUIRES_INSPECTION"         => JsSuccess[InspectionStatus](InspectionRequired)
        case "DOES_NOT_REQUIRE_INSPECTION" => JsSuccess[InspectionStatus](InspectionNotNeeded)
        case "INSPECTION_STATUS_PENDING"   => JsSuccess[InspectionStatus](InspectionPending)
        case e                             => JsError(s"Invalid InspectionStatus: $e")
      }

    override def writes(inspectionStatus: InspectionStatus): JsValue =
      inspectionStatus match {
        case InspectionRequired  => JsString("REQUIRES_INSPECTION")
        case InspectionNotNeeded => JsString("DOES_NOT_REQUIRE_INSPECTION")
        case InspectionPending   => JsString("INSPECTION_STATUS_PENDING")
      }
  }
}
