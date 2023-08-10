/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.config

import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.errors.error_template
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler

import javax.inject.{Inject, Singleton}

@Singleton
class ErrorHandler @Inject()(val messagesApi: MessagesApi, implicit val appConfig: AppConfig, errorTemplate: error_template)
    extends FrontendErrorHandler {
  override def standardErrorTemplate(
    pageTitle:        String,
    heading:          String,
    message:          String
  )(implicit request: Request[_]): Html =
    errorTemplate(pageTitle, heading, message)
}
