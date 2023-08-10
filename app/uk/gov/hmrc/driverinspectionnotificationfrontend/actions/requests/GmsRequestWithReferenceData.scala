/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.actions.requests

import play.api.mvc.{Request, WrappedRequest}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.GvmsReferenceData

case class GmsRequestWithReferenceData[A](referenceData: GvmsReferenceData, request: Request[A]) extends WrappedRequest[A](request)
