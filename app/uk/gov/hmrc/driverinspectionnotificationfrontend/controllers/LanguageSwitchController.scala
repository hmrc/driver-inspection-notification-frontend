/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.controllers

import play.api.i18n.Lang
import uk.gov.hmrc.play.language.{LanguageController, LanguageUtils}
import play.api.mvc.ControllerComponents
import com.google.inject.Inject
import uk.gov.hmrc.driverinspectionnotificationfrontend.config.AppConfig

import javax.inject.Singleton

@Singleton
class LanguageSwitchController @Inject()(appConfig: AppConfig, languageUtils: LanguageUtils, cc: ControllerComponents)
    extends LanguageController(languageUtils, cc) {
  import appConfig._

  override def fallbackURL: String = routes.SearchController.show().url

  override protected def languageMap: Map[String, Lang] =
    if (appConfig.otherLanguageSupportEnabled) {
      supportedLanguage.map(t => t -> Lang(t)).toMap
    } else {
      Map("en" -> Lang("en"))
    }

}
