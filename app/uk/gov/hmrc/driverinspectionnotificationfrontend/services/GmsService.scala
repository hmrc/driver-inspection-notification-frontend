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

package uk.gov.hmrc.driverinspectionnotificationfrontend.services

import cats.data.EitherT
import uk.gov.hmrc.driverinspectionnotificationfrontend.connectors.GoodsMovementSystemConnector
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.InspectionResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class GmsService @Inject() (
  goodsMovementSystemConnector: GoodsMovementSystemConnector
) {

  def getInspectionStatus(gmrId: String)(implicit hc: HeaderCarrier): EitherT[Future, GmrErrors, InspectionResponse] =
    EitherT(goodsMovementSystemConnector.getInspectionStatus(gmrId))
}
