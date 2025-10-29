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

package uk.gov.hmrc.driverinspectionnotificationfrontend

import cats.implicits._
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import org.apache.pekko.util.Timeout
import org.apache.poi.ss.usermodel._
import org.scalatest.BeforeAndAfterAll
import play.api.i18n.Messages
import play.api.i18n.Messages.UrlMessageSource
import play.api.{Configuration, Logger}
import uk.gov.hmrc.driverinspectionnotificationfrontend.helpers.{BaseSpec, Commands}

import java.io.File
import scala.jdk.CollectionConverters._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

class MessagesSpec extends BaseSpec with Commands with BeforeAndAfterAll {

  override def configuration: Configuration = Configuration(ConfigFactory.parseResources("test-application.conf"))

  override implicit val defaultAwaitTimeout: Timeout = 120 seconds

  lazy val englishMessages: Map[String, String] = getMessages("messages")

  if (configuration.get[Boolean]("content.useCheckout")) {
    logger.warn("running use checkout")
    await(executeCommand("./get-current-message-files-git-checkout.sh"))
  }

  lazy val stream   = Try(this.getClass.getResourceAsStream("/content_diff_file.xlsx")).getOrElse(fail("content difference file is required"))
  lazy val workbook = Try(WorkbookFactory.create(stream)).getOrElse(fail("content difference file is required"))

  def csv(languageColumnName: String): Map[String, Option[String]] = {

    def getCellValue(row: Row)(n: Int): Option[String] = {
      val formatter = new DataFormatter()

      val optCell: Option[Cell] = Option(row.getCell(n, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
      optCell.map(cell => formatter.formatCellValue(cell).trim)
    }

    def getCellIndex(row: Row)(predicate: String => Boolean): Option[Int] =
      row.asScala
        .find(cell => getCellValue(row)(cell.getColumnIndex).fold(false)(predicate))
        .map(_.getColumnIndex)

    val sheet: Sheet = workbook.getSheet(configuration.get[String]("content.tab"))

    val keyIndex = getCellIndex(sheet.asScala.head)(_.equalsIgnoreCase("key")).getOrElse(fail(s"could not find column for key"))
    val langIndex =
      getCellIndex(sheet.asScala.head)(_.equalsIgnoreCase(languageColumnName)).getOrElse(fail(s"could not find column for $languageColumnName"))

    sheet.asScala.tail.flatMap { row =>
      val optKey   = getCellValue(row)(keyIndex)
      val optValue = getCellValue(row)(langIndex)

      (for {
        key <- optKey
        content = optValue
      } yield key -> content).toList.filter(x => x._2.exists(_.trim.nonEmpty))
    }.toMap
  }

  def localMessageFileChanges(mainMessages: Map[String, String], localMessages: Map[String, String]): Seq[(String, Option[String])] = {
    val mainMessageWithFilterUnicode  = mainMessages.view.mapValues(m => Some(filterMessageKeySpecialCharacters(filterUnicodeCharacters(m))))
    val localMessageWithFilterUnicode = localMessages.view.mapValues(m => Some(filterMessageKeySpecialCharacters(filterUnicodeCharacters(m))))
    localMessageWithFilterUnicode.toList diff mainMessageWithFilterUnicode.toList
  }

  type Error[A] = Either[List[String], A]

  lazy val allLanguages = List(
    ("English", "", englishMessages),
    ("Welsh", ".cy", getMessages("messages.cy")),
    ("Bulgarian", ".bg", getMessages("messages.bg")),
    ("Croatian", ".hr", getMessages("messages.hr")),
    ("Czech", ".cs", getMessages("messages.cs")),
    ("French", ".fr", getMessages("messages.fr")),
    ("German", ".de", getMessages("messages.de")),
    ("Hungarian", ".hu", getMessages("messages.hu")),
    ("Lithuanian", ".lt", getMessages("messages.lt")),
    ("Romanian", ".ro", getMessages("messages.ro")),
    ("Polish", ".pl", getMessages("messages.pl")),
    ("Spanish", ".es", getMessages("messages.es"))
  )

  "content management" when {

    allLanguages.foreach {
      case (language, extension, messages) =>
        s"using $language" should {
          s"have all the same keys that are in the english file" in {
            /* For debugging purpose
            logger.info(s"Missing keys in $language: \n${(englishMessages.keys.toList diff messages.keys.toList).sorted.mkString("\n")}")
            logger.info(s"Missing keys in English: \n${(messages.keys.toList diff englishMessages.keys.toList).sorted.mkString("\n")}")
             */
            englishMessages.keys.toList should contain theSameElementsAs messages.keys.toList
          }

          s"have all fields successfully translated" in {
            val result = messages.filter {
              case (key, value) => value.equalsIgnoreCase("TODO")
            }


            logger.info(s"Keys left to translate\n${result.keys.toList.mkString("\n")}")

            result shouldBe Map()
          }

          val mainMessages: Map[String, String] = getMessages(s"current_messages$extension")
          val messageDiff = localMessageFileChanges(mainMessages, messages)
          if (messageDiff.nonEmpty) {
            s"the differences between main messages file ($language) and local updated one should be the same as the csv diff content" in {
              logger.info(s"$language differences since main: " + messageDiff.mkString("\n"))

              val content = csv(language).toList

              logger.info(s"$language differences with csv " + (content diff messageDiff).size.toString)
              logger.info(s"$language differences with csv: " + (content diff messageDiff).sortBy(_._1).mkString("\n"))
              logger.info(s"$language differences with messages " + (messageDiff diff content).size.toString)
              logger.info(s"$language differences with messages: " + (messageDiff diff content).mkString("\n"))
              messageDiff should contain theSameElementsAs content
            }
          }
        }
    }

    "check for duplicates" should {
      lazy val sameContent: Map[String, List[String]] =
        englishMessages
          .groupBy { case (_, v) => v }
          .filter { case (_, v) => v.size > 1 }
          .map { case (key, v) => key -> v.keys.toList }
          .toMap

      allLanguages.foreach {
        case (lang, _, messages) =>
          s"report the keys sharing the same content in English, but having different content in $lang" in {

            val exemptKeys = Try(configuration.get[Seq[String]](s"content.exemptions.$lang")).getOrElse(Seq.empty)

            val expectedDuplicatedContent = sameContent.map {
              case (content, keys) =>
                content -> keys.filter(key => !exemptKeys.contains(key)).map(key => key -> messages.getOrElse(key, "MISSING")).toMap
            }

            val nonDuplicatedContent = expectedDuplicatedContent.filter(_._2.values.toList.distinct.size > 1)

            if (nonDuplicatedContent.values.map(_.size).sum > 0) {
              logger.info("======= The following keys share the same content in English, but have different content in other languages: ====")
              logger.info(nonDuplicatedContent.mkString("\n"))
              logger.info("effected keys")
              logger.info(nonDuplicatedContent.values.flatMap(_.keys).mkString("\n"))
              logger.info("spreadsheet values")
              nonDuplicatedContent.foreach {
                case (content, map) =>
                  map.foreach {
                    case (key, translation) =>
                      logger.info(s"$key | $content | $translation")
                  }
              }
            }

            withClue(s"$lang does not have expected duplicate content") {
              nonDuplicatedContent.values.map(_.size).sum should not be >(0)
            }
          }
      }
    }
  }

  private def filterMessageKeySpecialCharacters(str: String) = {
    val messageSpecialCharacter = "'"
    str
      .replaceAll(messageSpecialCharacter, "")
  }

  private def filterUnicodeCharacters(str: String): String = {
    val NSBP    = "\u00a0"
    val newLine = "\n"
    str
      .replaceAll(NSBP, " ")
      .replaceAll(newLine, " ")
  }

  def getMessages(fileName: String): Map[String, String] =
    Try(Messages.parse(UrlMessageSource(this.getClass.getClassLoader.getResource(fileName)), "")).toEither
      .bifoldMap(Left.apply, identity)
      .leftMap(error => {
        logger.warn(s"could not find $fileName", error)
        Map.empty[String, String]
      })
      .merge

  override def afterAll(): Unit =
    await(
      Future.traverse(List(
        Try(new File(this.getClass.getResource("/current_messages").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.cy").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.bg").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.hr").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.cs").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.fr").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.de").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.hu").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.lt").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.ro").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.pl").toURI)).toList,
        Try(new File(this.getClass.getResource("/current_messages.es").toURI)).toList,
      ).flatten) { file =>
        Future {
          val removed = FileUtils.deleteQuietly(file)
          logger.info(s"Deleting file ${file.getName}, remove: $removed")
          removed
        }
      })

}
