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

package uk.gov.hmrc.driverinspectionnotificationfrontend.config

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.Singleton

class GuiceModule extends AbstractModule {

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
