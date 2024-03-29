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

package uk.gov.hmrc.driverinspectionnotificationfrontend.connectors

import uk.gov.hmrc.driverinspectionnotificationfrontend.connectors.httpreads.CustomEitherHttpReads
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.InspectionResponse
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http._

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GoodsMovementSystemConnector @Inject() (
  @Named("gmsUrl") gmsBaseUrl: String,
  httpClient:                  HttpClient
)(implicit ec: ExecutionContext)
    extends CustomEitherHttpReads {

  private val url = s"$gmsBaseUrl/goods-movement-system"

  implicit val errorThrowingHttpResponse: HttpReads[HttpResponse] =
    HttpReadsInstances.throwOnFailure(HttpReadsInstances.readEitherOf(HttpReadsInstances.readRaw))

  def getInspectionStatus(gmrId: String)(implicit headerCarrier: HeaderCarrier): Future[Either[GmrErrors, InspectionResponse]] =
    httpClient.GET[Either[GmrErrors, InspectionResponse]](s"$url/driver/movements/$gmrId/inspection")

}
