/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend

import akka.util.Timeout
import cats.implicits._
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
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

  val logger = Logger(this.getClass.getName)

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
    val mainMessageWithFilterUnicode  = mainMessages.mapValues(m => Some(filterMessageKeySpecialCharacters(filterUnicodeCharacters(m))))
    val localMessageWithFilterUnicode = localMessages.mapValues(m => Some(filterMessageKeySpecialCharacters(filterUnicodeCharacters(m))))
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
            println(s"Missing keys in $language: \n${(englishMessages.keys.toList diff messages.keys.toList).sorted.mkString("\n")}")
            println(s"Missing keys in English: \n${(messages.keys.toList diff englishMessages.keys.toList).sorted.mkString("\n")}")
             */
            englishMessages.keys.toList should contain theSameElementsAs messages.keys.toList
          }

          s"have all fields successfully translated" in {
            val result = messages.filter {
              case (key, value) => value.equalsIgnoreCase("TODO")
            }

            println("")
            println(s"Keys left to translate\n${result.keys.toList.mkString("\n")}")
            println("")
            result shouldBe Map()
          }

          val mainMessages: Map[String, String] = getMessages(s"current_messages$extension")
          val messageDiff = localMessageFileChanges(mainMessages, messages)
          if (messageDiff.nonEmpty) {
            s"the differences between main messages file ($language) and local updated one should be the same as the csv diff content" in {
              println(s"$language differences since main: " + messageDiff.mkString("\n"))

              val content = csv(language).toList

              println(s"$language differences with csv " + (content diff messageDiff).size.toString)
              println(s"$language differences with csv: " + (content diff messageDiff).sortBy(_._1).mkString("\n"))
              println(s"$language differences with messages " + (messageDiff diff content).size.toString)
              println(s"$language differences with messages: " + (messageDiff diff content).mkString("\n"))
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
              println("======= The following keys share the same content in English, but have different content in other languages: ====")
              println(nonDuplicatedContent.mkString("\n"))
              println("effected keys")
              println(nonDuplicatedContent.values.flatMap(_.keys).mkString("\n"))
              println("spreadsheet values")
              nonDuplicatedContent.foreach {
                case (content, map) =>
                  map.foreach {
                    case (key, translation) =>
                      println(s"$key | $content | $translation")
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
          println(s"Deleting file ${file.getName}, remove: $removed")
          removed
        }
      })

}
