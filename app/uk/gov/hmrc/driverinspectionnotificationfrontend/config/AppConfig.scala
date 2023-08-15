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
