import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import play.sbt.PlayImport.PlayKeys.playDefaultPort
import sbt.Keys.evictionErrorLevel

val appName = "driver-inspection-notification-frontend"

lazy val ContentTests = config("content") extend (Test)

def contentTestSettings(enableLicenseHeaders: Boolean = true): Seq[Setting[?]] =
  inConfig(ContentTests)(Defaults.testSettings) ++
    Seq(
      ContentTests / unmanagedSourceDirectories ++= Seq(baseDirectory.value / "content", baseDirectory.value / "test-common"),
      ContentTests / unmanagedResourceDirectories += baseDirectory.value / "test-resources",
      DefaultBuildSettings.addTestReportOption(ContentTests, "content-test-reports")
    ) ++
    (if (enableLicenseHeaders) {
       headerSettings(ContentTests) ++
         automateHeaderSettings(ContentTests)
     } else Seq.empty)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "3.5.0",
    ScoverageKeys.coverageExcludedFiles :=
      "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;" +
        "app.*;.*BuildInfo.*;.*Routes.*;.*repositories.*;.*LanguageSwitchController;.*metrics.*;.*views.*;Reverse.*;" +
        ".*connectors.*;.*.models.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    playDefaultPort := 9004,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    Assets / pipelineStages := Seq(gzip)
  )
  .settings(SassKeys.generateSourceMaps := false)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .configs(ContentTests)
  .settings(contentTestSettings(): _*)
  .settings(routesImport += "uk.gov.hmrc.driverinspectionnotificationfrontend.config.Binders._")
  .settings(
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    IntegrationTest / unmanagedSourceDirectories :=
      (IntegrationTest / baseDirectory)(base => Seq(base / "it", base / "test-common")).value,
    Test / unmanagedSourceDirectories := (Test / baseDirectory)(base => Seq(base / "test", base / "test-common")).value,
    IntegrationTest / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports/html-it-report")
  )
  .settings(scalafmtOnCompile := true,
    scalacOptions += "-Wconf:src=routes/.*:s", //Silence all warnings in generated routes
    scalacOptions += "-Wconf:msg=unused import*&src=html/.*:s",
    scalacOptions += "-language:postfixOps", //fix scaladoc generation in jenkins
    scalacOptions += "-no-indent"
  )
  .disablePlugins(JUnitXmlReportPlugin)

evictionErrorLevel := Level.Warn
