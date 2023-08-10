/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.utils;

import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.languages._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.language.{Cy, En, Language}

class LanguageMapSpec extends BaseSpec {

  "LanguageMapSpec" should {

    val codeLangMap = Map[String, Language](
      "en" -> En,
      "cy" -> Cy,
      "bg" -> Bg,
      "hr" -> Hr,
      "cs" -> Cs,
      "fr" -> Fr,
      "de" -> De,
      "hu" -> Hu,
      "lt" -> Lt,
      "pl" -> Pl,
      "ro" -> Ro,
      "es" -> Es
    )

    "should include all available languages" in {
      codeLangMap.foreach {
        case (key, language) => LanguageMap.get(key) shouldBe language
      }
    }
  }
}
