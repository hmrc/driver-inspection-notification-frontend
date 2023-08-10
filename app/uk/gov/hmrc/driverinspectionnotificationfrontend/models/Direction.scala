/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.models

import play.api.libs.json._

sealed trait Direction extends Product with Serializable {
  def value: String
}

object Direction {

  case object UK_INBOUND extends Direction {
    val value: String = this.toString
  }

  case object UK_OUTBOUND extends Direction {
    val value: String = this.toString
  }

  case object GB_TO_NI extends Direction {
    val value: String = this.toString
  }

  case object NI_TO_GB extends Direction {
    val value: String = this.toString
  }

  implicit val format: Format[Direction] = new Format[Direction] {

    override def writes(o: Direction): JsValue = JsString(o.value)

    override def reads(json: JsValue): JsResult[Direction] =
      json.validate[String].flatMap {
        case UK_INBOUND.value  => JsSuccess(UK_INBOUND)
        case UK_OUTBOUND.value => JsSuccess(UK_OUTBOUND)
        case GB_TO_NI.value    => JsSuccess(GB_TO_NI)
        case NI_TO_GB.value    => JsSuccess(NI_TO_GB)
      }
  }
}
