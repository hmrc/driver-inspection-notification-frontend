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

import play.api.Logger
import play.api.libs.json.JsValue
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Failure, Success, Try}
import cats.implicits._

trait CustomEitherHttpReads {

  val logger = Logger(this.getClass.getName)

  private def isCustomError(json: => JsValue)(predicate: String => Boolean): Boolean =
    Try((json \ "code").asOpt[String].exists(predicate)) match {
      case Success(true) =>
        logger.info(json.toString())
        true
      case Success(false) => false
      case Failure(_)     => false
    }

  implicit def readEitherOf[E, P](implicit customErrors: PartialFunction[HttpResponse, E], rds: HttpReads[P]): HttpReads[Either[E, P]] =
    for {
      response <- HttpReads[HttpResponse]
      result <- customErrors
                  .andThen(_.asLeft[P])
                  .andThen(HttpReads.pure(_))
                  .applyOrElse[HttpResponse, HttpReads[Either[E, P]]](response, _ => rds.map(_.asRight[E]))
    } yield result

  implicit def retrieveGmrErrorPartialFunction[P]: PartialFunction[HttpResponse, GmrErrors] = {

    case object GmrNotFoundError {
      def unapply(arg: HttpResponse): Boolean =
        isCustomError(arg.json)(_ === "GMR_NOT_FOUND")
    }

    { case GmrNotFoundError() =>
      GmrErrors.GmrNotFound
    }
  }
}
