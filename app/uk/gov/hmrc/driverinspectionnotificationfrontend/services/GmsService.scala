/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.services

import cats.data.EitherT
import uk.gov.hmrc.driverinspectionnotificationfrontend.connectors.GoodsMovementSystemConnector
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.InspectionResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GmsService @Inject()(
  goodsMovementSystemConnector: GoodsMovementSystemConnector
)(implicit ec:                  ExecutionContext) {

  def getInspectionStatus(gmrId: String)(implicit hc: HeaderCarrier): EitherT[Future, GmrErrors, InspectionResponse] =
    EitherT(goodsMovementSystemConnector.getInspectionStatus(gmrId))
}
