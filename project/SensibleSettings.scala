// Copyright 2016 Sam Halliday
// Licence: http://www.apache.org/licenses/LICENSE-2.0
import scala.util.{ Properties, Try }
import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._

/**
 * A bunch of sensible defaults that fommil typically uses.
 *
 * TODO: integrate / contribute to the typelevel sbt plugin.
 */
object Sensible {

  lazy val settings = Seq(
    ivyLoggingLevel := UpdateLogging.Quiet,

    scalacOptions in Compile ++= Seq(
      "-encoding", "UTF-8",
      "-target:jvm-1.6",
      "-feature",
      "-deprecation",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-Xlint",
      "-Yinline-warnings",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      //"-Ywarn-numeric-widen", // noisy
      //"-Ywarn-value-discard", // will require a lot of work
      "-Xfuture"
    ) ++ {
        if (scalaVersion.value.startsWith("2.11")) Seq("-Ywarn-unused-import")
        else Nil
      } ++ {
        // fatal warnings can get in the way during the DEV cycle
        if (sys.env.contains("CI")) Seq("-Xfatal-warnings")
        else Nil
      },
    javacOptions in (Compile, compile) ++= Seq(
      "-source", "1.6", "-target", "1.6", "-Xlint:all", "-Werror",
      "-Xlint:-options", "-Xlint:-path", "-Xlint:-processing"
    ),
    javacOptions in doc ++= Seq("-source", "1.6"),

    javaOptions := Seq("-Xss2m", "-XX:MaxPermSize=256m", "-Xms1g", "-Xmx1g"),
    javaOptions += "-Dfile.encoding=UTF8",
    javaOptions ++= Seq("-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode"),
    javaOptions in run ++= yourkitAgent,

    maxErrors := 1,
    fork := true,

    // 4 x 1GB = 4GB
    concurrentRestrictions in Global := Seq(Tags.limitAll(4)),

    dependencyOverrides ++= Set(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.scala-lang" % "scala-library" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-lang" % "scalap" % scalaVersion.value,
      "org.scala-lang.modules" %% "scala-xml" % scalaModulesVersion,
      "org.scala-lang.modules" %% "scala-parser-combinators" % scalaModulesVersion,
      "org.scalamacros" %% "quasiquotes" % quasiquotesVersion
    ) ++ logback ++ guava ++ shapeless(scalaVersion.value)
  ) ++ inConfig(Test)(testSettings) ++ scalariformSettings

  // TODO: scalariformSettingsWithIt generalised
  def testSettings = Seq(
    parallelExecution := true,

    // one JVM per test suite
    fork := true,
    testForkedParallel := true,
    testGrouping <<= (
      definedTests,
      baseDirectory,
      javaOptions,
      outputStrategy,
      envVars,
      javaHome,
      connectInput
    ).map { (tests, base, options, strategy, env, javaHomeDir, connectIn) =>
        val opts = ForkOptions(
          bootJars = Nil,
          javaHome = javaHomeDir,
          connectInput = connectIn,
          outputStrategy = strategy,
          runJVMOptions = options,
          workingDirectory = Some(base),
          envVars = env
        )
        tests.map { test =>
          Tests.Group(test.name, Seq(test), Tests.SubProcess(opts))
        }
      },

    testOptions ++= noColorIfEmacs,
    testFrameworks := Seq(TestFrameworks.ScalaTest, TestFrameworks.JUnit)
  )

  val scalaModulesVersion = "1.0.4"
  val akkaVersion = "2.3.14"
  val streamsVersion = "1.0"
  val scalatestVersion = "2.2.6"
  val logbackVersion = "1.7.16"
  val quasiquotesVersion = "2.0.1"
  val guavaVersion = "18.0"

  val macroParadise = Seq(
    compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
  )
  def shapeless(scalaVersion: String) = {
    if (scalaVersion.startsWith("2.10.")) macroParadise
    else Nil
  } :+ "com.chuusai" %% "shapeless" % "2.2.5"
  val logback = Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.5",
    "org.slf4j" % "slf4j-api" % logbackVersion,
    "org.slf4j" % "jul-to-slf4j" % logbackVersion,
    "org.slf4j" % "jcl-over-slf4j" % logbackVersion
  )
  val guava = Seq(
    "com.google.guava" % "guava" % guavaVersion,
    "com.google.code.findbugs" % "jsr305" % "3.0.1" % "provided"
  )

  // TODO: automate testLibs as part of the testSettings
  def testLibs(config: String = "test") = Seq(
    "org.scalatest" %% "scalatest" % scalatestVersion % config,
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % config,
    "org.scalacheck" %% "scalacheck" % "1.12.5" % config,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % config,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % config
  ) ++ logback.map(_ % config)

  // e.g. YOURKIT_AGENT=/opt/yourkit/bin/linux-x86-64/libyjpagent.so
  val yourkitAgent = Properties.envOrNone("YOURKIT_AGENT").map { name =>
    val agent = file(name)
    require(agent.exists(), s"Yourkit agent specified ($agent) does not exist")
    Seq(s"-agentpath:${agent.getCanonicalPath}")
  }.getOrElse(Nil)

  // WORKAROUND: https://github.com/scalatest/scalatest/issues/511
  def noColorIfEmacs =
    if (sys.env.get("INSIDE_EMACS").isDefined)
      Seq(Tests.Argument(TestFrameworks.ScalaTest, "-oWF"))
    else
      Seq(Tests.Argument(TestFrameworks.ScalaTest, "-oF"))

}
