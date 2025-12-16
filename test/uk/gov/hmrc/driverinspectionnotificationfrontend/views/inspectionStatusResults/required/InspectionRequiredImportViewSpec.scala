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

package uk.gov.hmrc.driverinspectionnotificationfrontend.views.inspectionStatusResults.required

import org.jsoup.nodes.Document
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.Direction.UK_INBOUND
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.{Address, Location}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.views.InspectionDisplayGroup
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.views.InspectionDisplayGroup.*
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.ViewBehaviours

import java.time.LocalDate

class InspectionRequiredImportViewSpec extends ViewBehaviours {

  "/search results inspection required import page" should {
    val gmrId = "GMRB00QT08MS"

    val locationsByInspectionType = Map.empty[InspectionDisplayGroup, List[Location]]
    val view                      = inspectionRequiredImportView(gmrId, locationsByInspectionType, UK_INBOUND)
    val doc                       = view.asDocument

    behave like normalPage("inspection_needed_import.title", section = None)(view)
    behave like pageWithBackLink(view)
    behave like pageTechSupportLink(view)
    behave like pageFeedbackLink(view)

    "be rendered" should {

      def assertFirstSection(doc: Document, isDefraTransit: Boolean = false): Unit = {
        doc.getElementById("govuk-notification-banner-title").text()                                   should be("Inspection required")
        doc.getElementsByClass("govuk-notification-banner__content").first().lastElementChild().text() should be("GMR ID: GMRB00QT08MS")
        val whatNext = doc.getElementsByTag("h2").get(1)
        whatNext.text() should be("What to do next")
        if (isDefraTransit) {
          whatNext.nextElementSiblings().first().text() should be(
            "Speak to your haulier or importer to find out which Border Control Post (BCP) or other inspection location you need to attend on arrival."
          )
          whatNext.nextElementSiblings().get(1).text() should be(
            "Goods needed for inspection must be kept in the same condition as when they were imported."
          )
        } else {
          whatNext.nextElementSiblings().first().text() should be("Report to your inspection site(s) in the order shown.")
          whatNext.nextElementSiblings().get(1).text() should be(
            "Check which inspection site(s) you need to attend at your border location of arrival. You may have to drive to an inland border facility (IBF) or customs checking facility."
          )
          whatNext.nextElementSiblings().get(2).text() should be(
            "Goods needed for inspection must be kept in the same condition as when they were imported."
          )
        }

        doc.getElementsByClass("govuk-warning-text").first().text() should be(
          "! Warning You may be charged a penalty if you do not arrive for an inspection"
        )
      }

      def assertBottomSection(document: Document): Unit = {
        val searchLink = doc.getElementsByAttributeValue("href", "/search").first()
        searchLink.text() should be("Check another GMR")

        val inspectionHeading2 = doc.getElementsByTag("h3").get(1)
        inspectionHeading2.text()                      should be("Attending an inland border facility (IBF) check")
        inspectionHeading2.nextElementSibling().text() should be("If you have to attend an IBF, you can:")

        val ibfActionsBullet = inspectionHeading2.nextElementSibling().nextElementSibling().children
        ibfActionsBullet.first().text() should be("find more information about IBFs")
        ibfActionsBullet.first().firstElementChild().attr("href") should be(
          "https://www.gov.uk/government/publications/attending-an-inland-border-facility"
        )
        ibfActionsBullet.get(1).text() should be("check if there are any delays at an IBF")
        ibfActionsBullet.get(1).firstElementChild().attr("href") should be(
          "https://www.gov.uk/guidance/check-if-there-are-any-delays-at-an-inland-border-facility"
        )

        val insetText = doc.getElementsByClass("govuk-inset-text").first()
        insetText.text() should be("If you have to attend an IBF in the south east area, you need to report to Sevington unless exempt.")

        val exemptBullets = insetText.nextElementSibling().nextElementSibling().children
        exemptBullets.first().text() should be("the vehicle exceeds the size limit")
        exemptBullets.get(1).text()  should be("the vehicle contains hazardous goods")
        exemptBullets.get(2).text()  should be("you hold a commercial agreement with another inspection site")

        val vehicleExcluded = insetText.nextElementSibling().nextElementSibling().nextElementSibling()
        vehicleExcluded.text() should be("Find more information on if your vehicle could be exempt from attending Sevington.")
        vehicleExcluded.firstElementChild().attr("href") should be(
          "https://www.gov.uk/government/publications/attending-an-inland-border-facility/attending-an-inland-border-facility#if-your-vehicle-is-excluded"
        )

        val thirdH3 = doc.getElementsByTag("h3").get(2)
        thirdH3.text() should be("Ending transit movements")
        thirdH3.nextElementSibling().text() should be(
          "You must present the goods at the office of destination declared in the transit declaration. Check with your declarant."
        )
      }

      "and show content as expected when an inspection location is present" in {

        val locationForCustoms = Location(
          locationId = "2",
          locationDescription = "Belfast Location for Customs",
          address = Address(
            lines = List(
              "2 Shamrock Lane",
              "Waldo"
            ),
            town = Some("Belfast"),
            postcode = "NI2 6JG"
          ),
          locationType = "BCP",
          locationEffectiveFrom = LocalDate.parse("2020-01-01"),
          locationEffectiveTo = None,
          supportedInspectionTypeIds = List(
            "1",
            "9",
            "10",
            "11",
            "12",
            "17"
          ),
          requiredInspectionLocations = List(
            1, 9, 10, 11, 12
          )
        )
        val view = inspectionRequiredImportView(gmrId, Map(CUSTOMS -> List(locationForCustoms)), UK_INBOUND)
        val doc  = view.asDocument

        assertFirstSection(doc)

        val inpsectionSite = doc.getElementsByTag("h2").get(2)
        inpsectionSite.text() should be("Your inspection site")

        val inspectionHeading1 = doc.getElementsByTag("h3").first()
        inspectionHeading1.text() should be("For your customs inspection")

        val locationDetails = inspectionHeading1.nextElementSibling().children()
        locationDetails.first().text() should be("Belfast Location for Customs")
        locationDetails.get(1).text()  should be("2 Shamrock Lane")
        locationDetails.get(2).text()  should be("Waldo")
        locationDetails.get(3).text()  should be("Belfast")
        locationDetails.get(4).text()  should be("NI2 6JG")

        assertBottomSection(doc)
      }

      "and show content as expected when there are multiple inspection types present" in {

        val locationForCustoms = Location(
          locationId = "2",
          locationDescription = "Belfast Location for Customs",
          address = Address(
            lines = List(
              "2 Shamrock Lane",
              "Waldo"
            ),
            town = Some("Belfast"),
            postcode = "NI2 6JG"
          ),
          locationType = "BCP",
          locationEffectiveFrom = LocalDate.parse("2020-01-01"),
          locationEffectiveTo = None,
          supportedInspectionTypeIds = List(
            "1",
            "9",
            "10",
            "11",
            "12",
            "17"
          ),
          requiredInspectionLocations = List(
            1, 9, 10, 11, 12
          )
        )

        val location = Location(
          locationId = "2",
          locationDescription = "Belfast Location for Customs",
          address = Address(
            lines = List(
              "2 Shamrock Lane",
              "Waldo"
            ),
            town = Some("Belfast"),
            postcode = "NI2 6JG"
          ),
          locationType = "BCP",
          locationEffectiveFrom = LocalDate.parse("2020-01-01"),
          locationEffectiveTo = None,
          supportedInspectionTypeIds = List(
            "1",
            "9",
            "10",
            "11",
            "12",
            "17"
          ),
          requiredInspectionLocations = List(
            1, 9, 10, 11, 12
          )
        )
        val view = inspectionRequiredImportView(
          gmrId,
          Map(
            CUSTOMS       -> List(locationForCustoms),
            DEFRA         -> List(location),
            BF_TRANSIT    -> List(location),
            OGD           -> List(location),
            DEFRA_PLANTS  -> List(location),
            ATA           -> List(location),
            MTP           -> List(location),
            TIR           -> List(location),
            DBC           -> List(locationForCustoms),
            EIDR          -> List(locationForCustoms),
            EXEMPTION     -> List(locationForCustoms),
            EMPTY         -> List(locationForCustoms),
            DAERA         -> List(location),
            SnS           -> List(location),
            UKIMS         -> List(locationForCustoms),
            DEFRA_TRANSIT -> List.empty,
            DAERA_CERTEX  -> List(location)
          ),
          UK_INBOUND
        )
        val doc = view.asDocument

        assertFirstSection(doc)

        val inpsectionSite = doc.getElementsByTag("h2").get(2)
        inpsectionSite.text()                      should be("Your inspection sites")
        inpsectionSite.nextElementSibling().text() should be("The inspections must take place in the order shown.")

        val inspectionHeadings = doc.getElementsByTag("h3")
        inspectionHeadings.get(0).text()  should be("For your DEFRA transit inspection")
        inspectionHeadings.get(1).text()  should be("For your Border Force transit inspection")
        inspectionHeadings.get(2).text()  should be("For your TIR Carnet endorsement")
        inspectionHeadings.get(3).text()  should be("For your DAERA inspection")
        inspectionHeadings.get(4).text()  should be("For your ENS (Safety and Security) inspection")
        inspectionHeadings.get(5).text()  should be("For your ATA Carnet endorsement")
        inspectionHeadings.get(6).text()  should be("For your customs inspection")
        inspectionHeadings.get(7).text()  should be("For your DEFRA plants inspection")
        inspectionHeadings.get(8).text()  should be("For your DEFRA inspection")
        inspectionHeadings.get(9).text()  should be("For your customs inspection")
        inspectionHeadings.get(10).text() should be("For your customs inspection")
        inspectionHeadings.get(11).text() should be("For your customs inspection")
        inspectionHeadings.get(12).text() should be("For your Manual Transit Procedure endorsement")
        inspectionHeadings.get(13).text() should be("For other government department inspections")
        inspectionHeadings.get(14).text() should be("For your customs inspection")
        inspectionHeadings.get(15).text() should be("For your DAERA CERTEX inspection")
        inspectionHeadings.get(16).text() should be("For your customs inspection")

        val defraTransitInset = inspectionHeadings.get(0).nextElementSibling()
        defraTransitInset.text() should be(
          "Speak to your haulier or importer to find out which Border Control Post (BCP) or other inspection location you need to attend on arrival."
        )

        val locationDetails = inspectionHeadings.get(1).nextElementSibling().children()
        locationDetails.first().text() should be("Belfast Location for Customs")
        locationDetails.get(1).text()  should be("2 Shamrock Lane")
        locationDetails.get(2).text()  should be("Waldo")
        locationDetails.get(3).text()  should be("Belfast")
        locationDetails.get(4).text()  should be("NI2 6JG")

        assertBottomSection(doc)
      }

      "and show content as expected when there is only defra transit inspection type" in {

        val locationForDefraTransit = Location(
          locationId = "2",
          locationDescription = "Belfast Location for Customs",
          address = Address(
            lines = List(
              "2 Shamrock Lane",
              "Waldo"
            ),
            town = Some("Belfast"),
            postcode = "NI2 6JG"
          ),
          locationType = "BCP",
          locationEffectiveFrom = LocalDate.parse("2020-01-01"),
          locationEffectiveTo = None,
          supportedInspectionTypeIds = List(
            "1",
            "9",
            "10",
            "11",
            "12",
            "17"
          ),
          requiredInspectionLocations = List(
            1, 9, 10, 11, 12
          )
        )
        val view = inspectionRequiredImportView(gmrId, Map(DEFRA_TRANSIT -> List(locationForDefraTransit)), UK_INBOUND)
        val doc  = view.asDocument

        assertFirstSection(doc, isDefraTransit = true)

        val inspectionHeading1 = doc.getElementsByTag("h3").first()
        inspectionHeading1.text() should be("For your DEFRA transit inspection")

        val insetText = doc.getElementsByClass("govuk-inset-text").first()
        insetText.text() should be(
          "Ask your haulier or importer to check the Import of products, animals, food and feed system (IPAFFS) to find out if you have any further inspections you need to attend."
        )

        assertBottomSection(doc)
      }

      "and show content as expected when there is defra transit and other inspection types" in {

        val locationForDefraTransit = Location(
          locationId = "2",
          locationDescription = "Belfast Location 2",
          address = Address(
            lines = List(
              "2 Shamrock Lane",
              "Waldo"
            ),
            town = Some("Belfast"),
            postcode = "NI2 6JG"
          ),
          locationType = "BCP",
          locationEffectiveFrom = LocalDate.parse("2020-01-01"),
          locationEffectiveTo = None,
          supportedInspectionTypeIds = List(
            "18",
            "5"
          ),
          requiredInspectionLocations = List(
            2
          )
        )

        val locationForDefraPlants = Location(
          locationId = "1",
          locationDescription = "Belfast Location 1",
          address = Address(
            lines = List(
              "1 Shamrock Lane",
              "Waldo"
            ),
            town = Some("Belfast"),
            postcode = "NI1 6JG"
          ),
          locationType = "BCP",
          locationEffectiveFrom = LocalDate.parse("2020-01-01"),
          locationEffectiveTo = None,
          supportedInspectionTypeIds = List(
            "18",
            "5"
          ),
          requiredInspectionLocations = List(
            1
          )
        )
        val view = inspectionRequiredImportView(
          gmrId,
          Map(DEFRA_TRANSIT -> List(locationForDefraTransit), DEFRA_PLANTS -> List(locationForDefraPlants)),
          UK_INBOUND
        )
        val doc = view.asDocument

        assertDoesNotContainText(doc, "Belfast Location 2")

        assertFirstSection(doc)

        val inspectionHeading1 = doc.getElementsByTag("h3").first()
        inspectionHeading1.text() should be("For your DEFRA transit inspection")

        val insetText = doc.getElementsByClass("govuk-inset-text").first()
        insetText.text() should be(
          "Ask your haulier or importer to check the Import of products, animals, food and feed system (IPAFFS) to find out if you have any further inspections you need to attend."
        )

        assertBottomSection(doc)
      }

    }
  }
}
