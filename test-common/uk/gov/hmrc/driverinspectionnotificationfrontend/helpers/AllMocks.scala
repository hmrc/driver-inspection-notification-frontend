/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import org.mockito.Mockito
import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.driverinspectionnotificationfrontend.config.AppConfig
import uk.gov.hmrc.driverinspectionnotificationfrontend.connectors.{GoodsMovementSystemConnector, GoodsMovementSystemReferenceDataConnector}
import uk.gov.hmrc.driverinspectionnotificationfrontend.services.{GmsReferenceDataService, GmsService}

trait AllMocks extends BeforeAndAfterEach {
  self: BaseSpec =>

  val mockAppConfig:              AppConfig                                 = mock[AppConfig]
  val mockGmsConnector:           GoodsMovementSystemConnector              = mock[GoodsMovementSystemConnector]
  val mockReferenceDataConnector: GoodsMovementSystemReferenceDataConnector = mock[GoodsMovementSystemReferenceDataConnector]
  val mockGmsService:             GmsService                                = mock[GmsService]
  val mockReferenceDataService:   GmsReferenceDataService                   = mock[GmsReferenceDataService]

  override protected def beforeEach(): Unit =
    Seq(
      mockAppConfig,
      mockGmsConnector,
      mockReferenceDataConnector,
      mockGmsService,
      mockReferenceDataService
    ).foreach(Mockito.reset(_))

}
