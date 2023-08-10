/*
 * Copyright 2023 HM Revenue & Customs
 *
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
class GoodsMovementSystemConnector @Inject()(
  @Named("gmsUrl") gmsBaseUrl: String,
  httpClient:                  HttpClient
)(implicit ec:                 ExecutionContext)
    extends CustomEitherHttpReads {

  private val url = s"$gmsBaseUrl/goods-movement-system"

  implicit val errorThrowingHttpResponse: HttpReads[HttpResponse] =
    HttpReadsInstances.throwOnFailure(HttpReadsInstances.readEitherOf(HttpReadsInstances.readRaw))

  def getInspectionStatus(gmrId: String)(implicit headerCarrier: HeaderCarrier): Future[Either[GmrErrors, InspectionResponse]] =
    httpClient.GET[Either[GmrErrors, InspectionResponse]](s"$url/driver/movements/$gmrId/inspection")

}
