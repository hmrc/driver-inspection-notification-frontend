/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.views.helpers

import play.api.i18n.Messages
import play.api.mvc.RequestHeader
import uk.gov.hmrc.hmrcfrontend.config.AccessibilityStatementConfig
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.footer.FooterItem

import javax.inject.{Inject, Singleton}

@Singleton
class NonStandardHmrcFooterItems @Inject()(accessibilityStatementConfig: AccessibilityStatementConfig) {

  def get(implicit messages: Messages, request: RequestHeader): Seq[FooterItem] =
    Seq(
      footerItemForKey("cookies"),
      accessibilityLink,
      footerItemForKey("privacy"),
      footerItemForKey("termsConditions"),
      footerItemForKey("govukHelp"),
      footerItemForKey("contact")
    ).flatMap(_.toList)

  private def accessibilityLink(implicit messages: Messages, request: RequestHeader): Option[FooterItem] =
    accessibilityStatementConfig.url
      .map(
        href => FooterItem(Some(messages("footer.accessibility.text")), Some(href))
      )

  private def footerItemForKey(item: String)(implicit messages: Messages): Option[FooterItem] =
    Some(
      FooterItem(
        text = Some(messages(s"footer.$item.text")),
        href = Some(messages(s"footer.$item.url"))
      ))
}
