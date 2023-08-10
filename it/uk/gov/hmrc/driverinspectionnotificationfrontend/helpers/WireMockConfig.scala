/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

trait WireMockConfig {
  me: BaseISpec with WireMockSupport =>

  additionalAppConfig ++= setWireMockPort("goods-movement-system", "goods-movement-system-reference-data")

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def setWireMockPort(services: String*): Map[String, Any] =
    services.foldLeft(Map.empty[String, Any]) {
      case (map, service) => map + (s"microservice.services.$service.port" -> mockServerPort)
    }
}
