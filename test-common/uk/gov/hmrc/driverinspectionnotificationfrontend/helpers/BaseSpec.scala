/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, EitherValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier

trait BaseSpec
    extends AnyWordSpecLike
    with Matchers
    with FakeObjects
    with DefaultAwaitTimeout
    with FutureAwaits
    with TestTemplateComponents
    with Configs
    with MockitoSugar
    with ScalaFutures
    with AllMocks
    with BeforeAndAfterEach
    with EitherValues
    with StubMessageControllerComponents {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

}
