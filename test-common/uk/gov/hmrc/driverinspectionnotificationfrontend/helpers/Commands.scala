/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import java.io.{BufferedReader, File, InputStreamReader}
import scala.concurrent.Future

trait Commands {
  self: BaseSpec =>

  def executeCommand(cmd: String): Future[Unit] =
    executeCommand(cmd, ".")

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def executeCommand(cmd: String, dir: String): Future[Unit] =
    executeCommand(cmd, null, dir)

  @SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.While"))
  def executeCommand(cmd: String, params: Array[String], strDir: String): Future[Unit] = {

    println("Running " + cmd)
    Future {
      val dir = new File(strDir)
      val p:      Process        = Runtime.getRuntime.exec(cmd, params, dir)
      val reader: BufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream))

      var line: String = reader.readLine()
      while (line != null) {
        println(line)
        line = reader.readLine()
      }
      p.waitFor()
      println("-------> Done.")
    }
  }
}
