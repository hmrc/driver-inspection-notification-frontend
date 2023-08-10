/*
 * Copyright 2023 HM Revenue & Customs
 *
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
