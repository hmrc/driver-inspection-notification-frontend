/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.utils

import play.api.i18n.Messages

import java.time.{Instant, LocalDate, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {

  private def zoneId = ZoneId.of("CET")

  def instantFormatDate(instant: Instant)(implicit messages: Messages): LocalDate = instant.atZone(zoneId).toLocalDate

  def instantFormatHours(instant: Instant)(implicit messages: Messages): String = hoursFormatter(messages.lang.locale).format(instant)

  def hoursFormatter(locale: Locale): DateTimeFormatter =
    DateTimeFormatter
      .ofPattern("HH:mm z")
      .withLocale(locale)
      .withZone(zoneId)
}
