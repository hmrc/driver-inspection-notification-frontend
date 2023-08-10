/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.connectors

import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.GvmsReferenceData
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GoodsMovementSystemReferenceDataConnector @Inject()(
  @Named("gmsReferenceDataUrl") gmsReferenceDataBaseUrl: String,
  httpClient:                                            HttpClient
)(implicit executionContext:                             ExecutionContext) {

  private val url = s"$gmsReferenceDataBaseUrl/goods-movement-system-reference-data"

  def getReferenceData(implicit hc: HeaderCarrier): Future[GvmsReferenceData] =
    httpClient.GET[GvmsReferenceData](s"$url/reference-data")
}
