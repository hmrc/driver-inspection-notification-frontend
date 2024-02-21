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

  def countSubstring(substring: String, content: String): Int =
    content.sliding(substring.length).count(_ == substring)
}
