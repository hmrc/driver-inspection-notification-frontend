import sbt.Keys.evictionErrorLevel

resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scalameta"      % "sbt-scalafmt"       % "2.5.2")
addSbtPlugin("org.playframework"  % "sbt-plugin"         % "3.0.1")
addSbtPlugin("com.typesafe.sbt"   % "sbt-gzip"           % "1.0.2")
addSbtPlugin("io.github.irundaia" % "sbt-sassify"        % "1.5.2")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"      % "2.0.9")
addSbtPlugin("uk.gov.hmrc"        % "sbt-auto-build"     % "3.22.0")
addSbtPlugin("uk.gov.hmrc"        % "sbt-distributables" % "2.5.0")
addSbtPlugin("org.wartremover"    % "sbt-wartremover"    % "3.1.6")

evictionErrorLevel := Level.Warn
