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

enum Direction:
  case UK_INBOUND
  case UK_OUTBOUND
  case GB_TO_NI
  case NI_TO_GB

object Direction:

  implicit val format: Format[Direction] = new Format[Direction] {
    override def writes(o: Direction): JsValue = JsString(o.toString)

    override def reads(json: JsValue): JsResult[Direction] =
      try
        json.validate[String].map(Direction.valueOf)
      catch {
        case e: IllegalArgumentException => JsError(s"Direction is not recognised: $e")
      }
  }
