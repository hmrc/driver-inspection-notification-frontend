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

package uk.gov.hmrc.driverinspectionnotificationfrontend.actions

import play.api.i18n.MessagesApi
import play.api.mvc._
import uk.gov.hmrc.driverinspectionnotificationfrontend.actions.requests.GmsRequestWithReferenceData
import uk.gov.hmrc.driverinspectionnotificationfrontend.services.GmsReferenceDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendHeaderCarrierProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReferenceDataTransformer @Inject()(
  gmsReferenceDataService:       GmsReferenceDataService,
  mcc:                           ControllerComponents
)(implicit val executionContext: ExecutionContext)
    extends ActionTransformer[Request, GmsRequestWithReferenceData]
    with FrontendHeaderCarrierProvider
    with Results {

  val parser: BodyParser[AnyContent] = mcc.parsers.anyContent

  val messagesApi: MessagesApi = mcc.messagesApi

  override protected def transform[A](request: Request[A]): Future[GmsRequestWithReferenceData[A]] =
    gmsReferenceDataService.getReferenceData(hc(request)).map { referencedata =>
      new GmsRequestWithReferenceData[A](referencedata, request)
    }
}
