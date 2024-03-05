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

package uk.gov.hmrc.driverinspectionnotificationfrontend.controllers

import cats.implicits._
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.driverinspectionnotificationfrontend.actions.GmsActionBuilders
import uk.gov.hmrc.driverinspectionnotificationfrontend.actions.requests.GmsRequestWithReferenceData
import uk.gov.hmrc.driverinspectionnotificationfrontend.config.AppConfig
import uk.gov.hmrc.driverinspectionnotificationfrontend.errorhandlers.GmrErrors
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.Direction._
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.inspections.{InspectionStatus, ReportLocations}
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.referencedata.GvmsReferenceData
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.views.InspectionDisplayGroup
import uk.gov.hmrc.driverinspectionnotificationfrontend.models.{Direction, InspectionResponse}
import uk.gov.hmrc.driverinspectionnotificationfrontend.services.{GmsReferenceDataService, GmsService}
import uk.gov.hmrc.driverinspectionnotificationfrontend.utils.EitherUtils.partitionAndExtract
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.inspectionStatusResults._
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.inspectionStatusResults.cleared._
import uk.gov.hmrc.driverinspectionnotificationfrontend.views.html.inspectionStatusResults.required._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SearchResultController @Inject() (
  mcc:                                 MessagesControllerComponents,
  gmsActionBuilders:                   GmsActionBuilders,
  gmsService:                          GmsService,
  referenceDataService:                GmsReferenceDataService,
  inspection_required_import:          inspection_required_import,
  inspection_required_export:          inspection_required_export,
  inspection_not_needed_page_gb_to_ni: inspection_not_needed_gb_to_ni,
  inspection_not_needed_page_import:   inspection_not_needed_import,
  inspection_not_needed_page_export:   inspection_not_needed_export,
  inspection_pending_page:             inspection_pending
)(implicit appConfig: AppConfig, executionContext: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  import gmsActionBuilders._

  private val logger = Logger(this.getClass.getName)

  def result(gmrId: String, checkedStatusAgain: Boolean): Action[AnyContent] = withReferenceData.async { implicit request =>
    gmsService
      .getInspectionStatus(gmrId)
      .fold(
        ErrorHandling.handleGmrErrors(gmrId),
        {
          case InspectionResponse(direction, InspectionStatus.InspectionRequired, reportToLocations) =>
            Ok(inspectionRequired(gmrId, direction, reportToLocations.getOrElse(Nil)))
          case InspectionResponse(direction, InspectionStatus.InspectionNotNeeded, _) =>
            Ok(inspectionNotRequired(gmrId, direction))
          case InspectionResponse(_, InspectionStatus.InspectionPending, _) =>
            Ok(inspection_pending_page(gmrId, checkedStatusAgain))
        }
      )
  }

  private def inspectionRequired(gmrId: String, direction: Direction, reportToLocations: List[ReportLocations])(implicit
    request: GmsRequestWithReferenceData[_]
  ) = {
    implicit val referenceData: GvmsReferenceData = request.referenceData
    direction match {
      case UK_INBOUND | GB_TO_NI | NI_TO_GB =>
        referenceDataService.getInspectionData(reportToLocations) match {
          case Nil =>
            logger.info(s"Missing or empty reportToLocations field in InspectionResponse for gmr with id $gmrId & direction $direction")
            inspection_required_import(Some(gmrId), Map(), direction)
          case listOfEithers =>
            val (inspectionTypesNotFound, inspectionTypesAndLocations) = partitionAndExtract(listOfEithers)
            if (inspectionTypesNotFound.nonEmpty)
              logger.warn(s"Inspection types with ids [${inspectionTypesNotFound.map(_.inspectionTypeId).mkString(",")}] not found in reference data")
            inspectionTypesAndLocations match {
              case Nil => inspection_required_import(Some(gmrId), Map(), direction)
              case list =>
                val inspectionLocations = list.map { case (inspectionType, eitherLocations) =>
                  val (locationsNotFound, locations) = partitionAndExtract(eitherLocations)
                  if (locationsNotFound.nonEmpty)
                    logger.warn(s"Locations with ids [${locationsNotFound.map(_.locationId).mkString(",")}] not found in reference data")
                  (InspectionDisplayGroup(inspectionType), locations)
                }.toMap
                inspection_required_import(Some(gmrId), inspectionLocations, direction)
            }
        }
      case UK_OUTBOUND =>
        referenceDataService.getInspectionData(reportToLocations) match {
          case Nil =>
            logger.info(s"Missing or empty reportToLocations field in InspectionResponse for gmr with id $gmrId & direction $direction")
            inspection_required_export(Some(gmrId), Map())
          case listOfEithers =>
            val (inspectionTypesNotFound, inspectionTypesAndLocations) = partitionAndExtract(listOfEithers)
            if (inspectionTypesNotFound.nonEmpty)
              logger.warn(s"Inspection types with ids [${inspectionTypesNotFound.map(_.inspectionTypeId).mkString(",")}] not found in reference data")
            inspectionTypesAndLocations match {
              case Nil => inspection_required_export(Some(gmrId), Map())
              case list =>
                val inspectionLocations = list.map { case (inspectionType, eitherLocations) =>
                  val (locationsNotFound, locations) = partitionAndExtract(eitherLocations)
                  if (locationsNotFound.nonEmpty)
                    logger.warn(s"Locations with ids [${locationsNotFound.map(_.locationId).mkString(",")}] not found in reference data")
                  (InspectionDisplayGroup(inspectionType), locations)
                }.toMap
                inspection_required_export(Some(gmrId), inspectionLocations)
            }
        }
    }
  }

  private def inspectionNotRequired(gmrId: String, direction: Direction)(implicit request: Request[_]) =
    direction match {
      case GB_TO_NI              => inspection_not_needed_page_gb_to_ni(Some(gmrId))
      case UK_INBOUND | NI_TO_GB => inspection_not_needed_page_import(Some(gmrId))
      case UK_OUTBOUND           => inspection_not_needed_page_export(Some(gmrId))
    }

  object ErrorHandling {
    def handleGmrErrors(gmrId: String): GmrErrors => Result = { case GmrErrors.GmrNotFound =>
      Redirect(routes.SearchController.show()).flashing(("not-found", gmrId))
    }
  }
}
