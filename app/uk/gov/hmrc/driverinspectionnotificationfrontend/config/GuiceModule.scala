/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.config

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.Singleton

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  @Provides
  @Named("gmsUrl")
  @Singleton
  def registerGmsUrlProvider(servicesConfig: ServicesConfig): String =
    servicesConfig.baseUrl("goods-movement-system")

  @Provides
  @Named("gmsReferenceDataUrl")
  @Singleton
  def registerGmsReferenceDataUrlProvider(servicesConfig: ServicesConfig): String =
    servicesConfig.baseUrl("goods-movement-system-reference-data")

}
