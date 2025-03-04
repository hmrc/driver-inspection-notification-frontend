import sbt._

object AppDependencies {

  private val bootstrapPlay30Version = "9.7.0"
  private val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"   %% s"bootstrap-frontend-$playVersion" % bootstrapPlay30Version,
    "uk.gov.hmrc"   %% s"play-frontend-hmrc-$playVersion" % "11.2.0",
    "org.typelevel" %% "cats-core"                        % "2.10.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% s"bootstrap-test-$playVersion" % bootstrapPlay30Version  % "test, it",
    "org.jsoup"              % "jsoup"                   % "1.18.1"                      % "test, it",
    "org.apache.poi"         % "poi"                     % "5.3.0"                       % "content",
    "org.apache.poi"         % "poi-ooxml"               % "5.3.0"                       % "content"
  )
}
