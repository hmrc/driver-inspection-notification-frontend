/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata

import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec

class LocationSpec extends BaseSpec {

  "getPostcode" should {
    "return empty if the postcode is n/a regardless its case sensitiveness" in {
      val postcodes = List("n/a", "N/A", "n/A", "N/a")
      postcodes foreach { postcode =>
        val address = Address(List("abc"), None, postcode = postcode)
        address.getPostcode shouldBe ("")
      }
    }
    "return the postcode if the postcode is not n/a regardless its case sensitiveness" in {
      val address = Address(List("abc"), None, postcode = "ab1 3ef")
      address.getPostcode shouldBe ("ab1 3ef")
    }
  }
}
