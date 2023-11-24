import sbt.Keys.evictionErrorLevel

resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scalameta"      % "sbt-scalafmt"       % "2.4.6")
addSbtPlugin("com.typesafe.play"  % "sbt-plugin"         % "2.8.19")
addSbtPlugin("com.typesafe.sbt"   % "sbt-gzip"           % "1.0.2")
addSbtPlugin("io.github.irundaia" % "sbt-sassify"        % "1.5.2")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"      % "2.0.5")
addSbtPlugin("uk.gov.hmrc"        % "sbt-auto-build"     % "3.15.0")
addSbtPlugin("uk.gov.hmrc"        % "sbt-distributables" % "2.2.0")
addSbtPlugin("org.wartremover"    % "sbt-wartremover"    % "3.0.9")

evictionErrorLevel := Level.Warn
