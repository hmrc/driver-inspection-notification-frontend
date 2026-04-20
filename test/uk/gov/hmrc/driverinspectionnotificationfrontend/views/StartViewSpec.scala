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

import uk.gov.hmrc.driverinspectionnotificationfrontend.views

class StartViewSpec extends ViewBehaviours {

  "/start page" when {

    val langs = Seq("en", "cy", "bg", "hr", "cs", "fr", "de", "hu", "lt", "pl", "ro", "es")
    val view  = startView(true, langs)
    val doc   = view.asDocument

    behave like normalPage("start_page.title", section = None)(view)
    behave like pageWithoutBackLink(view)
    behave like pageTechSupportLink(view)

    "rendered" should {
      "renders language links and their visually hidden labels" in {

        val languagelLabels: List[String] = List(
          "Welsh (Cymraeg)",
          "Bulgarian (български)",
          "Croatian (Hrvatski)",
          "Czech (Čeština)",
          "French (Français)",
          "German (Deutsch)",
          "Hungarian (Magyar)",
          "Lithuanian (Lietuviškai)",
          "Polish (Polski)",
          "Romanian (Română)",
          "Spanish (Español)"
        )

        val accessibleChangeLabels: List[String] = List(
          "Newid yr iaith ir Gymraeg",
          "Промяна на езика на български",
          "Promijeni jezik na hrvatski",
          "Změnit jazyk na češtinu",
          "Changer de langue et passer au français",
          "Sprache in Deutsch ändern",
          "Váltás magyar nyelvre",
          "Pakeisti kalbą į lietuvių",
          "Zmień język na polski",
          "Schimbați limba în română",
          "Cambiar el idioma a español"
        )

        langs.tail.zip(languagelLabels).zip(accessibleChangeLabels).map { case ((lang, label), accessibleLabel) =>
          assertLanguageLink(doc, lang, label, accessibleLabel)
        }
      }

      "show content as expected" in {
        val govukBodyParagraphs = doc.getElementsByClass("govuk-body")
        val h2s                 = doc.getElementsByTag("h2")
        val h3s                 = doc.getElementsByTag("h3")
        h2s.first().text()                should be("Use this service to check if you need an inspection of your goods")
        h2s.get(1).text()                 should be("Before you start")
        h2s.get(2).text()                 should be("If the movement needs an inspection")
        h3s.first().text()                should be("If you are entering the UK:")
        h3s.get(1).text()                 should be("If you are leaving the UK:")
        govukBodyParagraphs.get(1).text() should be("This service is also available in:")
        govukBodyParagraphs.get(2).text() should be(
          "Check this towards the end of your crossing, around 10 minutes before you reach your border location of arrival."
        )
        govukBodyParagraphs.get(3).text() should be("Check this before arriving at your border location of departure.")
        govukBodyParagraphs.get(4).text() should be("You will need to enter your goods movement reference (GMR).")
        govukBodyParagraphs.get(6).text() should be(
          "You will need to report to the appropriate inspection location. This will either be at your border location of arrival, border location of departure or at an inland border facility (IBF) (opens in new tab) ."
        )
        val ibfLink = govukBodyParagraphs.get(6).getElementsByClass("govuk-link").first()
        ibfLink.attr("href") should be("https://www.gov.uk/government/publications/attending-an-inland-border-facility")
        val responses = govukBodyParagraphs.get(5)
        responses.text() should be("You will get one of these responses:")
        val bullets = responses.nextElementSibling()
        bullets.child(0).text() should be("inspection needed")
        bullets.child(1).text() should be("no inspection needed")
        bullets.child(2).text() should be("your inspection status is not ready yet")

        val startButton = doc.getElementsByClass("govuk-button--start").first()
        startButton.text()       should be("Start now")
        startButton.attr("href") should be("/search")
      }
    }
  }
}
