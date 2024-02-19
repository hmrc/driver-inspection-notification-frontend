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

class InspectionTypeSpec extends BaseSpec {

  "descriptionMappedToCustoms" should {

    "return the description in lower case, and return as 'customs' if the inspection type is any of the followings: CUSTOMS, DBC, EIDR, EXEMPTION or EMPTY" in {
      List(
        (InspectionType("1", "description"), "customs"),
        (InspectionType("2", "DEFRA"), "defra"),
        (InspectionType("3", "TRANSIT"), "transit"),
        (InspectionType("4", "OGD"), "ogd"),
        (InspectionType("5", "DEFRA_PLANTS"), "defra_plants"),
        (InspectionType("6", "ATA"), "ata"),
        (InspectionType("7", "SAD"), "sad"),
        (InspectionType("8", "TIR"), "tir"),
        (InspectionType("9", "DBC"), "customs"),
        (InspectionType("10", "EIDR"), "customs"),
        (InspectionType("11", "EXEMPTION"), "customs"),
        (InspectionType("12", "EMPTY"), "customs"),
        (InspectionType("13", "DAERA"), "daera")
      ).map(i => i._1.descriptionMappedToCustoms shouldBe i._2)
    }
  }
}