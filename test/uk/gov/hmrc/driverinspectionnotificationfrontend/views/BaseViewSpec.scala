/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.driverinspectionnotificationfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.Assertion
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec

import scala.language.implicitConversions

class BaseViewSpec extends BaseSpec {

  implicit val request: FakeRequest[?] = FakeRequest()

  implicit class HtmlOps(html: Html) {
    def asDocument: Document = Jsoup.parse(html.toString())
  }

  def assertLink(link: Element, href: String, text: String): Assertion = {
    link.attr("href")           should be(href)
    link.attr("data-linktext")  should be(text)
    link.text()                 should be(text)
    link.hasClass("govuk-link") should be(true)
  }

  def assertLanguageLink(doc: Document, lang: String, label: String, accessibleLabel: String): Assertion = {
    val link: Element = doc.getElementsByAttributeValue("href", s"/language/$lang").first()
    link.text()                          should be(label)
    link.hasClass("govuk-link")          should be(true)
    link.previousElementSibling().text() should be(accessibleLabel)
  }

  def assertDoesNotContainText(doc: Document, text: String) =
    assert(!doc.toString.contains(text), "\n\ntext " + text + " was rendered on the page.\n")
}
