import play.sbt.routes.RoutesKeys.routes
import sbt._
import sbt.Keys._
import wartremover.Wart._
import wartremover.Warts
import wartremover.WartRemover.autoImport.{wartremoverErrors, wartremoverExcluded, wartremoverWarnings}
object  WartRemoverSettings {

  lazy val settings = {

    val warnings = {
      (Compile / compile / wartremoverWarnings) ++= Seq(
        Throw,
        Equals,
        PlatformDefault,
        StringPlusAny,
        ToString,
        JavaSerializable,
        ListAppend,
        SeqApply
      )
    }

    val errors = {
      (Compile / compile / wartremoverErrors) ++= Warts.allBut(
        Any,
        Throw,
        ToString,
        ImplicitParameter,
        PublicInference,
        Equals,
        Overloading,
        FinalCaseClass,
        Nothing,
        Serializable,
        Product,
        NonUnitStatements,
        PlatformDefault,
        StringPlusAny,
        ToString,
        JavaSerializable,
        ListAppend,
        SeqApply
      )
    }

    val routesAndFoldersExclusions = wartremoverExcluded ++=
      (Compile / routes).value


    val testExclusions = {
      val errorsExcluded = (Test / compile / wartremoverErrors) --= Seq(
          DefaultArguments,
          Serializable,
          Product,
          OptionPartial,
          GlobalExecutionContext
        )
      val warningsExcluded = (Test / compile / wartremoverWarnings) --= Seq(
        OptionPartial,
        Throw,
        Equals
      )
      errorsExcluded ++ warningsExcluded
    }

    warnings ++ errors ++ routesAndFoldersExclusions ++ testExclusions
  }
}