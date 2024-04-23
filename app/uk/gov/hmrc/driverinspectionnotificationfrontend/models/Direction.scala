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

package uk.gov.hmrc.driverinspectionnotificationfrontend.models

import play.api.libs.json._

sealed trait Direction extends Product with Serializable {
  def value:             String
  override def toString: String = value
}

object Direction {

  case object UK_INBOUND extends Direction {
    val value: String = "UK_INBOUND"
  }

  case object UK_OUTBOUND extends Direction {
    val value: String = "UK_OUTBOUND"
  }

  case object GB_TO_NI extends Direction {
    val value: String = "GB_TO_NI"
  }

  case object NI_TO_GB extends Direction {
    val value: String = "NI_TO_GB"
  }

  implicit val format: Format[Direction] = new Format[Direction] {

    override def writes(o: Direction): JsValue = JsString(o.value)

    override def reads(json: JsValue): JsResult[Direction] =
      json.validate[String].flatMap {
        case UK_INBOUND.value  => JsSuccess(UK_INBOUND)
        case UK_OUTBOUND.value => JsSuccess(UK_OUTBOUND)
        case GB_TO_NI.value    => JsSuccess(GB_TO_NI)
        case NI_TO_GB.value    => JsSuccess(NI_TO_GB)
        case e                 => JsError(s"Direction $e is not recognised")
      }
  }
}
