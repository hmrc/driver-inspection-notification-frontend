/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.actions

import play.api.mvc._
import uk.gov.hmrc.driverinspectionnotificationfrontend.actions.requests.GmsRequestWithReferenceData

import javax.inject.{Inject, Singleton}

@Singleton
class GmsActionBuilders @Inject()(
  defaultActionBuilder:     DefaultActionBuilder,
  referenceDataTransformer: ReferenceDataTransformer,
) {
  val withReferenceData: ActionBuilder[GmsRequestWithReferenceData, AnyContent] =
    defaultActionBuilder.andThen(referenceDataTransformer)

}
