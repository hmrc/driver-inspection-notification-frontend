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

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.views

import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.InspectionType

trait InspectionDisplayGroup {
  val value: String
}

trait InspectionCustomsGroup extends InspectionDisplayGroup {
  val value: String = "customs"
}

object InspectionDisplayGroup {

  case object CUSTOMS extends InspectionCustomsGroup

  case object DEFRA extends InspectionDisplayGroup {
    val value = "defra"
  }

  case object BF_TRANSIT extends InspectionDisplayGroup {
    val value = "bf_transit"
  }

  case object OGD extends InspectionDisplayGroup {
    val value = "ogd"
  }

  case object DEFRA_PLANTS extends InspectionDisplayGroup {
    val value = "defra_plants"
  }

  case object ATA extends InspectionDisplayGroup {
    val value = "ata"
  }

  case object SAD extends InspectionDisplayGroup {
    val value = "sad"
  }

  case object TIR extends InspectionDisplayGroup {
    val value = "tir"
  }

  case object DBC extends InspectionCustomsGroup

  case object EIDR extends InspectionCustomsGroup

  case object EXEMPTION extends InspectionCustomsGroup

  case object EMPTY extends InspectionCustomsGroup

  case object UKIMS extends InspectionCustomsGroup

  case object DAERA extends InspectionDisplayGroup {
    val value = "daera"
  }

  case object SnS extends InspectionDisplayGroup {
    val value = "sns"
  }

  case object DEFRA_TRANSIT extends InspectionDisplayGroup {
    val value = "defra_transit"
  }

  def apply(inspectionType: InspectionType): InspectionDisplayGroup =
    inspectionType.inspectionTypeId match {
      case "1" | "9" | "10" | "11" | "12" | "17" => CUSTOMS
      case "2"                                   => DEFRA
      case "3"                                   => BF_TRANSIT
      case "4"                                   => OGD
      case "5"                                   => DEFRA_PLANTS
      case "6"                                   => ATA
      case "7"                                   => SAD
      case "8"                                   => TIR
      case "13"                                  => DAERA
      case "16"                                  => SnS
      case "18"                                  => DEFRA_TRANSIT
    }
}
