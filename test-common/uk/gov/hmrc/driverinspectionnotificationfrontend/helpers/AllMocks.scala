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

import org.mockito.Mockito
import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.driverinspectionnotificationfrontend.config.AppConfig
import uk.gov.hmrc.driverinspectionnotificationfrontend.connectors.{GoodsMovementSystemConnector, GoodsMovementSystemReferenceDataConnector}
import uk.gov.hmrc.driverinspectionnotificationfrontend.services.{GmsReferenceDataService, GmsService}

import scala.annotation.nowarn

trait AllMocks extends BeforeAndAfterEach {
  self: BaseSpec =>

  val mockAppConfig:              AppConfig                                 = mock[AppConfig]
  val mockGmsConnector:           GoodsMovementSystemConnector              = mock[GoodsMovementSystemConnector]
  val mockReferenceDataConnector: GoodsMovementSystemReferenceDataConnector = mock[GoodsMovementSystemReferenceDataConnector]
  val mockGmsService:             GmsService                                = mock[GmsService]
  val mockReferenceDataService:   GmsReferenceDataService                   = mock[GmsReferenceDataService]

  @nowarn
  override protected def beforeEach(): Unit =
    Seq(
      mockAppConfig,
      mockGmsConnector,
      mockReferenceDataConnector,
      mockGmsService,
      mockReferenceDataService
    ).foreach(Mockito.reset(_))

}
