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

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import org.apache.pekko.stream.testkit.NoMaterializer
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
      .toOption
      .getOrElse(throw new Exception("messages resource could not be parsed"))

  implicit lazy val messagesApi: MessagesApi =
    new DefaultMessagesApi(messages = Map("default" -> messages))

  implicit val messagesImpl: MessagesImpl = MessagesImpl(Lang.defaultLang, messagesApi)

  def stubMessagesControllerComponents(
    bodyParser:      BodyParser[AnyContent] = stubBodyParser(AnyContentAsEmpty),
    playBodyParsers: PlayBodyParsers = stubPlayBodyParsers(NoMaterializer),
    fileMimeTypes:   FileMimeTypes = new DefaultFileMimeTypes(FileMimeTypesConfiguration())
  )(implicit
    executionContext: ExecutionContext
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
