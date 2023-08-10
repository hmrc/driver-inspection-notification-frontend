/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import scala.collection.mutable

trait AdditionalAppConfig {
  @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.MutableDataStructures"))
  val additionalAppConfig: mutable.Map[String, Any] = mutable.Map.empty
}
