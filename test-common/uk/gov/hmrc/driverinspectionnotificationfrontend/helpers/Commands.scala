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

import play.api.Logging

import java.io.{BufferedReader, File, InputStreamReader}
import scala.concurrent.Future

trait Commands extends Logging {
  self: BaseSpec =>

  def executeCommand(cmd: String): Future[Unit] =
    executeCommand(cmd, ".")

  def executeCommand(cmd: String, dir: String): Future[Unit] =
    executeCommand(cmd, null, dir)

  def executeCommand(cmd: String, params: Array[String], strDir: String): Future[Unit] = {

    logger.info("Running " + cmd)
    Future {
      val dir = new File(strDir)
      val p:      Process        = Runtime.getRuntime.exec(cmd, params, dir)
      val reader: BufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream))

      var line: String = reader.readLine()
      while (line != null) {
        logger.info(line)
        line = reader.readLine()
      }
      p.waitFor()
      logger.info("-------> Done.")
    }
  }
}
