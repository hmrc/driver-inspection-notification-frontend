/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import akka.stream.testkit.NoMaterializer
import play.api.http.{DefaultFileMimeTypes, FileMimeTypes, FileMimeTypesConfiguration, HttpConfiguration}
import play.api.i18n.Messages.UrlMessageSource
import play.api.i18n._
import play.api.mvc._
import play.api.test.Helpers.{stubBodyParser, stubPlayBodyParsers}

import scala.concurrent.ExecutionContext

trait StubMessageControllerComponents extends Configs {

  val langs: Langs = new DefaultLangs()

  val httpConfiguration = new HttpConfiguration()

  implicit val messages: Map[String, String] =
    Messages
      .parse(UrlMessageSource(this.getClass.getClassLoader.getResource("messages")), "")
      .right
      .toOption
      .getOrElse(throw new Exception("messages resource could not be parsed"))

  implicit lazy val messagesApi: MessagesApi =
    new DefaultMessagesApi(messages = Map("default" -> messages))

  implicit val messagesImpl: MessagesImpl = MessagesImpl(Lang.defaultLang, messagesApi)

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  def stubMessagesControllerComponents(
    bodyParser:      BodyParser[AnyContent] = stubBodyParser(AnyContentAsEmpty),
    playBodyParsers: PlayBodyParsers        = stubPlayBodyParsers(NoMaterializer),
    fileMimeTypes:   FileMimeTypes          = new DefaultFileMimeTypes(FileMimeTypesConfiguration())
  )(
    implicit executionContext: ExecutionContext
  ): MessagesControllerComponents =
    DefaultMessagesControllerComponents(
      new DefaultMessagesActionBuilderImpl(bodyParser, messagesApi),
      DefaultActionBuilder(bodyParser),
      playBodyParsers,
      messagesApi,
      langs,
      fileMimeTypes,
      executionContext
    )

}
