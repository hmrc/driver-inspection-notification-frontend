/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.driverinspectionnotificationfrontend.controllers.routes
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(val configuration: Configuration, servicesConfig: ServicesConfig) {

  def otherLanguageSupportEnabled: Boolean = configuration.getOptional[Boolean]("features.other-language-support").getOrElse(false)

  def attendingInlandBorderFacilityUrl: String = configuration.get[String]("urls.attendingInlandBorderFacilityUrl")

  def checkDelaysInlandBorderFacilityUrl: String = configuration.get[String]("urls.checkDelaysInlandBorderFacilityUrl")

  def exitSurveyFeedback(): String =
    s"${configuration.get[String]("feedback-frontend.baseUrl")}/feedback/${configuration.get[String]("appName")}"

  def routeToSwitchLanguage = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  def supportedLanguage: Seq[String] = configuration.get[Seq[String]]("play.i18n.langs")

}
