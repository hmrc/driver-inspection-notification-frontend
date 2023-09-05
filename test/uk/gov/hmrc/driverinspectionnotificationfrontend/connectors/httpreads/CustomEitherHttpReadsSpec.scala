/*
 * Copyright 2023 HM Revenue & Customs
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
