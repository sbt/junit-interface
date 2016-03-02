import SonatypeSupport._
import sbt._
import sbt.Keys._
import sbt.ScriptedPlugin._
import util.Properties

object EnsimeSbtBuild extends Build {

  override val settings = super.settings ++ Seq(
    organization := "com.novocode",
    version := "1.0.0-SNAPSHOT",
    crossPaths := false,
    autoScalaLibrary := false
  ) ++ sonatype("sbt", "junit-interface", BSD2)

  lazy val root = Project("junit-interface", file(".")).
    settings(scriptedSettings ++ Sensible.settings ++ Seq(
      libraryDependencies ++= Seq(
        "junit" % "junit" % "4.12",
        "org.scala-sbt" % "test-interface" % "1.0"
      ),
      scriptedLaunchOpts := Seq(
        "-Dproject.version=" + version.value,
        "-XX:MaxPermSize=256m"
      ),
      scriptedBufferLog := false
    ))

}
