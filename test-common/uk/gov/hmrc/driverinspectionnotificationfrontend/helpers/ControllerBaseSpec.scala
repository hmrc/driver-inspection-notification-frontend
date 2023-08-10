/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, DefaultActionBuilder, Request, Result}
import uk.gov.hmrc.driverinspectionnotificationfrontend.actions.{GmsActionBuilders, ReferenceDataTransformer}
import uk.gov.hmrc.driverinspectionnotificationfrontend.actions.requests.GmsRequestWithReferenceData

import scala.concurrent.{ExecutionContext, Future}

class ControllerBaseSpec extends BaseSpec {

  def actionBuilders(): GmsActionBuilders =
    new GmsActionBuilders(
      mock[DefaultActionBuilder],
      mock[ReferenceDataTransformer],
    ) {
      override val withReferenceData: ActionBuilder[GmsRequestWithReferenceData, AnyContent] =
        new ActionBuilder[GmsRequestWithReferenceData, AnyContent] {
          override def parser: BodyParser[AnyContent] = stubMessagesControllerComponents().parsers.anyContent

          override def invokeBlock[A](request: Request[A], block: GmsRequestWithReferenceData[A] => Future[Result]): Future[Result] =
            block(GmsRequestWithReferenceData(gvmsReferenceData, request))

          override protected def executionContext: ExecutionContext = ec
        }
    }
}
