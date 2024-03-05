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

package uk.gov.hmrc.driverinspectionnotificationfrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.i18n.Messages.implicitMessagesProviderToMessages
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.driverinspectionnotificationfrontend.config.AppConfig
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors.GmrNotFound
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.forms.GmrSearchForm
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.search_page
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class SearchController @Inject() (
  mcc:        MessagesControllerComponents,
  searchPage: search_page
)(implicit appConfig: AppConfig)
    extends FrontendController(mcc) {

  implicit val ex: ExecutionContext = mcc.executionContext

  def show(gmrId: Option[String]): Action[AnyContent] = Action { implicit request =>
    request.flash.get("not-found") match {
      case Some(gmrId) =>
        BadRequest(searchPage(GmrErrorHandling.handlingGmrRetrievalFailure(gmrId)(GmrNotFound)))
      case None =>
        Ok(searchPage(gmrId.fold(GmrSearchForm.gmrSearchForm)(GmrSearchForm.gmrSearchForm.fill)))

    }
  }

  def submit(): Action[Map[String, Seq[String]]] = Action(parse.formUrlEncoded) { implicit request =>
    GmrSearchForm.gmrSearchForm
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(searchPage(formWithErrors)),
        gmrId => Redirect(routes.SearchResultController.result(gmrId, checkedStatusAgain = false))
      )
  }

  object GmrErrorHandling {
    def handlingGmrRetrievalFailure(value: String): GmrErrors => Form[String] = { case GmrNotFound =>
      GmrSearchForm.gmrSearchForm
        .withError(GmrSearchForm.field, "search_page.error.gmr_not_found")
        .fill(value)
    }
  }
}
