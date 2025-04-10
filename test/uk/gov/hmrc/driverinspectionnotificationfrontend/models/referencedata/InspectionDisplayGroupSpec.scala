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

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata

import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.views.InspectionDisplayGroup

class InspectionDisplayGroupSpec extends BaseSpec {

  "InspectionDisplayGroup" should {

    "return the correct inspection display type, and correct value description in lower case, and return as 'customs' if the inspection type is any of the followings: CUSTOMS, DBC, EIDR, EXEMPTION, UKIMS or EMPTY" in {
      List[(InspectionType, InspectionDisplayGroup, String)](
        (InspectionType("1", "description"), InspectionDisplayGroup.CUSTOMS, "customs"),
        (InspectionType("2", "DEFRA"), InspectionDisplayGroup.DEFRA, "defra"),
        (InspectionType("3", "BF_TRANSIT"), InspectionDisplayGroup.BF_TRANSIT, "bf_transit"),
        (InspectionType("4", "OGD"), InspectionDisplayGroup.OGD, "ogd"),
        (InspectionType("5", "DEFRA_PLANTS"), InspectionDisplayGroup.DEFRA_PLANTS, "defra_plants"),
        (InspectionType("6", "ATA"), InspectionDisplayGroup.ATA, "ata"),
        (InspectionType("7", "SAD"), InspectionDisplayGroup.SAD, "sad"),
        (InspectionType("8", "TIR"), InspectionDisplayGroup.TIR, "tir"),
        (InspectionType("9", "DBC"), InspectionDisplayGroup.CUSTOMS, "customs"),
        (InspectionType("10", "EIDR"), InspectionDisplayGroup.CUSTOMS, "customs"),
        (InspectionType("11", "EXEMPTION"), InspectionDisplayGroup.CUSTOMS, "customs"),
        (InspectionType("12", "EMPTY"), InspectionDisplayGroup.CUSTOMS, "customs"),
        (InspectionType("13", "DAERA"), InspectionDisplayGroup.DAERA, "daera"),
        (InspectionType("17", "UKIMS"), InspectionDisplayGroup.CUSTOMS, "customs")
      ).foreach { case (inspectionType, inspectionDisplayGroup, value) =>
        val result = InspectionDisplayGroup(inspectionType)
        result       shouldBe inspectionDisplayGroup
        result.value shouldBe value
      }
    }
  }
}
