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

import org.jsoup.nodes.Document
import org.scalatest.Assertion
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content

class ViewBehaviours extends BaseViewSpec {

  def normalPage(headingKey: String, headingArgs: Seq[String] = Seq(), section: Option[String] = None)(html: Html): Unit =
    "behave like a normal page" when {

      val document = html.asDocument

      "rendered" should {

        "display govuk header content" in {
          document.getElementsByClass("govuk-header__link govuk-header__service-name").text() shouldBe messagesImpl("service.name")
        }

        "display the correct browser title" in {
          assertEqualsMessage(document, "title", title(messagesImpl(headingKey, headingArgs*), section))
        }

        "display the correct page heading" in {
          assertPageHeadingEqualsMessage(document, headingKey, headingArgs*)
        }
      }
    }

  private def assertPageHeadingEqualsMessage(doc: Document, expectedMessageKey: String, args: Any*): Assertion = {
    val pageHeading = messagesImpl(expectedMessageKey, args*).replaceAll("&nbsp;", " ")
    val headers     = doc.getElementsByTag("h1")
    if (headers.isEmpty)
      doc.body().getElementsContainingOwnText(pageHeading).size shouldBe 1
    else {
      headers.size                               shouldBe 1
      headers.first.text.replaceAll("\u00a0", " ") should include(pageHeading)
    }
  }

  private def assertEqualsMessage(doc: Document, cssSelector: String, expectedMessageKey: String): Assertion =
    assertEqualsValue(doc, cssSelector, messagesImpl(expectedMessageKey))

  private def assertEqualsValue(doc: Document, cssSelector: String, expectedValue: String): Assertion = {
    val elements = doc.select(cssSelector)

    if (elements.isEmpty) throw new IllegalArgumentException(s"CSS Selector $cssSelector wasn't rendered.")

    assert(elements.first().html().replace("\n", "") == expectedValue)
  }

  def title(heading: String, section: Option[String] = None)(implicit messages: Messages) =
    section match {
      case Some(section) => s"$heading - $section - ${messages("service.name")} - ${messages("generic.title.suffix")}"
      case None          => s"$heading - ${messages("service.name")} - ${messages("generic.title.suffix")}"
    }

  def assertContainsRadioButton(
    html:      Html,
    id:        String,
    name:      String,
    value:     String,
    labelText: String,
    isChecked: Boolean,
    hint:      Option[Content] = None
  ): Assertion = {
    val doc = html.asDocument
    assertRenderedById(doc, id)
    val radio = doc.getElementById(id)
    assert(radio.attr("name") == name, s"\n\nElement $id does not have name $name")
    assert(radio.attr("value") == value, s"\n\nElement $id does not have value $value")
    hint.map { h =>
      if (hint.isDefined)
        assertContainsText(doc, h.asHtml.toString)
    }

    doc.select(s"label[for=$id]").text() shouldBe labelText

    if (isChecked)
      assert(radio.hasAttr("checked"), s"\n\nElement $id is not checked")
    else
      assert(!radio.hasAttr("checked"), s"\n\nElement $id is checked")
  }

  private def assertRenderedById(doc: Document, id: String) =
    assert(doc.getElementById(id).outerHtml().nonEmpty, "\n\nElement " + id + " was not rendered on the page.\n")

  private def assertContainsText(doc: Document, text: String) =
    assert(doc.toString.contains(text), "\n\ntext " + text + " was not rendered on the page.\n")

  def pageWithSubmitButton(html: Html, buttonText: String = "Continue"): Unit =
    "behave like a page with a submit button" should {
      s"have a button with $buttonText message" in {
        html.asDocument.getElementsByClass("govuk-button").last.text() shouldBe buttonText
      }
    }

  def pageWithBackLink(html: Html): Unit =
    "behave like a page with 'Back' link " should {
      "display 'Back' link as expected" in {
        html.asDocument.getElementsByClass("govuk-back-link").text() shouldBe "Back"
      }
    }

  def pageWithoutBackLink(html: Html): Unit =
    "behave like a page without a 'Back' link " should {
      "does *not* display a 'Back' link as expected" in {
        html.asDocument.getElementsByClass("govuk-back-link").size() shouldBe 0
      }
    }

  def assertDefaultLink(
    html: Html,
    text: String,
    url:  String
  ): Unit = {
    val link = html.asDocument.select(s"a[href*=$url]")
    link.text() shouldBe text
  }

  def pageTechSupportLink(
    html:           Html,
    text:           String = "Is this page not working properly? (opens in new tab)",
    techSupportUrl: String = "/contact/report-technical-problem?service=driver-inspection-notification-frontend"
  ): Unit =
    "behave like a page with an 'Tech Support' link " should {
      "display 'Tech Support' link as expected" in {
        val link = html.asDocument.select(s"a[href*=$techSupportUrl]")
        link.text() shouldBe text
      }
    }

  def pageFeedbackLink(
    html:        Html,
    text:        String = "What did you think of this service?",
    feedbackUrl: String = "/feedback/driver-inspection-notification-frontend"
  ): Unit =
    "behave like a page with an 'Feedback' link " should {
      "display 'Feedback survey' link as expected" in {
        val link = html.asDocument.select(s"a[href*=$feedbackUrl]")
        link.text() shouldBe text
      }
    }
}
