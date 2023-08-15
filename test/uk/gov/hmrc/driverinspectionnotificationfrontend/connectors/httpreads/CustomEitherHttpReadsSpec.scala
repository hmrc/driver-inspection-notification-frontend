/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.connectors.httpreads

import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.BaseSpec
import play.api.libs.json.Json
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.http.{HttpReadsInstances, HttpResponse}

class CustomEitherHttpReadsSpec extends BaseSpec {

  trait Setup {
    val reads = new CustomEitherHttpReads {}
  }

  "readsEitherOf" when {

    "retrieveGmrErrorPartialFunction" should {

      "read successful 200 response as successful right" in new Setup {
        val response = HttpResponse(200, "")

        val result = reads.readEitherOf(reads.retrieveGmrErrorPartialFunction, HttpReadsInstances.readRaw).read("GET", "http://localhost", response)

        result.right.value shouldBe response
      }

      "read unsuccessful responses and transform custom errors" in new Setup {
        Map(
          HttpResponse(404, Json.obj("code" -> "GMR_NOT_FOUND"), Map.empty[String, Seq[String]]) -> GmrErrors.gmrNotFound
        ).foreach {
          case (response, expected) =>
            val result = reads
              .readEitherOf(
                reads.retrieveGmrErrorPartialFunction,
                HttpReadsInstances.throwOnFailure(HttpReadsInstances.readEitherOf(HttpReadsInstances.readRaw)))
              .read("GET", "http://localhost", response)

            result.left.value shouldBe expected
        }
      }

    }
  }
}
