/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.controllers

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlMatching}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.{BaseISpec, WireMockConfig, WireMockHelper, WireMockSupport}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.InspectionStatus.{InspectionNotNeeded, InspectionPending, InspectionRequired}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.ReportLocations

import scala.concurrent.Future

class SearchResultControllerISpec extends BaseISpec with WireMockSupport with WireMockHelper with WireMockConfig {

  "GET /results/:gmrId" should {

    "Inspection pending" in {
      stubGet(
        "/goods-movement-system/driver/movements/GMRA00002KW2/inspection",
        Json.stringify(Json.toJson(inspectionResponse(inspectionStatus = InspectionPending))))

      stubGet("/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gvmsReferenceData)))

      val result: Future[Result] = callRoute(FakeRequest(routes.SearchResultController.result("GMRA00002KW2")))

      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      val content = contentAsString(result)
      content         should include("Your inspection status is not ready yet")
      charset(result) shouldBe Some("utf-8")
    }

    "Inspection required (with arrivals additional content enabled)" when {
      "reportLocations present" in {
        stubGet(
          "/goods-movement-system/driver/movements/GMRA00002KW2/inspection",
          Json.stringify(
            Json.toJson(inspectionResponse(inspectionStatus = InspectionRequired, reportToLocations = Some(List(ReportLocations("1", List("1")))))))
        )

        stubGet("/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gvmsReferenceData)))

        val result: Future[Result] = callRoute(FakeRequest(routes.SearchResultController.result("GMRA00002KW2")))

        status(result)      shouldBe OK
        contentType(result) shouldBe Some("text/html")
        val content = contentAsString(result)
        content         should include("The goods you are moving require an inspection")
        content         should include("Your inspection site")
        charset(result) shouldBe Some("utf-8")
      }

      "reportLocations absent" in {
        stubGet(
          "/goods-movement-system/driver/movements/GMRA00002KW2/inspection",
          Json.stringify(Json.toJson(inspectionResponse(inspectionStatus = InspectionRequired, reportToLocations = None)))
        )

        stubGet("/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gvmsReferenceData)))

        val result: Future[Result] = callRoute(FakeRequest(routes.SearchResultController.result("GMRA00002KW2")))

        status(result)      shouldBe OK
        contentType(result) shouldBe Some("text/html")
        val content = contentAsString(result)
        content         should include("The goods you are moving require an inspection")
        content         should include("What to do next")
        content         should include("Your inspection sites")
        charset(result) shouldBe Some("utf-8")
      }
    }

    "No inspection required" in {
      stubGet(
        "/goods-movement-system/driver/movements/GMRA00002KW2/inspection",
        Json.stringify(Json.toJson(inspectionResponse(inspectionStatus = InspectionNotNeeded)))
      )

      stubGet("/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gvmsReferenceData)))

      val result: Future[Result] = callRoute(FakeRequest(routes.SearchResultController.result("GMRA00002KW2")))

      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      val content = contentAsString(result)
      content         should include("No inspection needed")
      charset(result) shouldBe Some("utf-8")
    }

    "return 400 and the HTML for the page with validation errors" in {

      stubFor(
        get(urlMatching("/goods-movement-system/driver/movements/GMRA00002KW2/inspection"))
          .willReturn(
            aResponse().withStatus(404).withBody("""{"code": "GMR_NOT_FOUND"}""")
          ))

      stubGet("/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gvmsReferenceData)))

      val result: Future[Result] = callRoute(FakeRequest(routes.SearchResultController.result("GMRA00002KW2")))

      status(result)                 shouldBe SEE_OTHER
      redirectLocation(result)       shouldBe Some(routes.SearchController.show(None).url)
      flash(result).get("not-found") shouldBe Some("GMRA00002KW2")
    }

    "return 500 if connector returns 404 but no GMR_NOT_FOUND in the response error code" in {

      stubFor(
        get(urlMatching("/goods-movement-system/driver/movements/GMRA00002KW2/inspection"))
          .willReturn(
            aResponse().withStatus(404).withBody("""{"code": "blah"}""").withHeader("Content-Type", "application/json")
          ))

      stubGet("/goods-movement-system-reference-data/reference-data", Json.stringify(Json.toJson(gvmsReferenceData)))

      val result: Future[Result] = callRoute(FakeRequest(routes.SearchResultController.result("GMRA00002KW2")))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

}
