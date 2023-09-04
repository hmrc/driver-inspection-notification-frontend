/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside, Inspectors, LoneElement, OptionValues, Status => _}
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Request, Result, Results}
import play.api.test._
import play.api.{Application, Mode}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

abstract class BaseISpec
    extends AnyWordSpec
    with GuiceOneAppPerSuite
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with Matchers
    with Inspectors
    with FakeObjects
    with ScalaFutures
    with DefaultAwaitTimeout
    with Writeables
    with EssentialActionCaller
    with RouteInvokers
    with LoneElement
    with Inside
    with OptionValues
    with Results
    with Status
    with HeaderNames
    with MimeTypes
    with HttpProtocol
    with HttpVerbs
    with ResultExtractors
    with WireMockHelper
    with AdditionalAppConfig {

  implicit lazy val system:       ActorSystem       = ActorSystem()
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()
  implicit def ec:                ExecutionContext  = global

  additionalAppConfig ++= Map(
    "metrics.enabled"  -> false,
    "auditing.enabled" -> false,
  )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .disable[com.kenshoo.play.metrics.PlayModule]
      .configure(additionalAppConfig.toMap)
      .in(Mode.Test)
      .build()

  def callRoute[A](req: Request[A])(implicit app: Application, w: Writeable[A]): Future[Result] = {
    val errorHandler = app.errorHandler
    route(app, req) match {
      case None => fail("Route does not exist")
      case Some(fResult) =>
        fResult.recoverWith {
          case t: Throwable => errorHandler.onServerError(req, t)
        }
    }
  }
}