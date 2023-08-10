/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.driverinspectionnotificationfrontend.config.AppConfig
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.start_page
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}

@Singleton
class StartController @Inject()(mcc: MessagesControllerComponents, startPage: start_page)(implicit appConfig: AppConfig)
    extends FrontendController(mcc) {

  def show(): Action[AnyContent] = Action { implicit request =>
    val langList         = appConfig.supportedLanguage
    val otherLangSupport = appConfig.otherLanguageSupportEnabled
    Ok(startPage(otherLangSupport, langList))
  }

}
