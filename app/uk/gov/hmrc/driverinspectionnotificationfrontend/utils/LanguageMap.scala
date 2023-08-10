/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.utils

import uk.gov.hmrc.hmrcfrontend.views.viewmodels.language.{Cy, En, Language}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.languages._

object LanguageMap {

  private val codeLangMap = Map[String, Language](
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

  def get(code: String): Language = codeLangMap(code)

}
