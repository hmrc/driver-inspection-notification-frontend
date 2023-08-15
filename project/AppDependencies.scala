import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"   %% "bootstrap-frontend-play-28" % "7.13.0",
    "org.typelevel" %% "cats-core"                  % "2.9.0",
    "uk.gov.hmrc"   %% "play-frontend-hmrc"         % "6.5.0-play-28"
  )

  val test = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"             % "test, it",
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % "7.13.0"            % "test, it",
    "org.scalatest"          %% "scalatest"              % "3.2.15"             % "test, it",
    "org.jsoup"              % "jsoup"                   % "1.15.4"            % "test, it",
    "com.typesafe.play"      %% "play-test"              % PlayVersion.current % "test, it",
    "org.scalatestplus"      %% "mockito-3-4"            % "3.2.10.0"           % "test, it",
    "com.vladsch.flexmark"   % "flexmark-all"            % "0.64.0"            % "test, it",
    "com.github.tomakehurst" % "wiremock-standalone"     % "2.27.2"            % IntegrationTest,
    "org.apache.poi"         % "poi"                     % "5.2.3"              % "content",
    "org.apache.poi"         % "poi-ooxml"               % "5.2.3"              % "content"
  )
}
