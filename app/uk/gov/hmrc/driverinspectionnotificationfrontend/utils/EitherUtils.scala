/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.utils

object EitherUtils {

  @SuppressWarnings(Array("org.wartremover.warts.EitherProjectionPartial"))
  def partitionAndExtract[A, B](list: List[Either[A, B]]): (List[A], List[B]) = {
    val (lefts, rights) = list.partition(_.isLeft)
    (lefts.map(_.left.get), rights.map(_.right.get))
  }
}
