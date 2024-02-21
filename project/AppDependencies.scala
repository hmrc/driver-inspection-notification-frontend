import sbt._

object AppDependencies {

  private val bootstrapPlay30Version = "8.4.0"
  private val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"   %% s"bootstrap-frontend-$playVersion" % bootstrapPlay30Version,
    "uk.gov.hmrc"   %% s"play-frontend-hmrc-$playVersion" % "8.5.0",
    "org.typelevel" %% "cats-core"                        % "2.10.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% s"bootstrap-test-$playVersion" % bootstrapPlay30Version  % "test, it",
    "org.jsoup"              % "jsoup"                   % "1.17.2"                      % "test, it",
    "org.apache.poi"         % "poi"                     % "5.2.5"                       % "content",
    "org.apache.poi"         % "poi-ooxml"               % "5.2.5"                       % "content"
  )
}
