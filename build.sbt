ThisBuild / version := "0.12-SNAPSHOT"
ThisBuild / organization := "com.novocode"
ThisBuild / description := "An implementation of sbt's test interface for JUnit 4"

lazy val `junit-interface` = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(nocomma {
    name := "junit-interface"

    autoScalaLibrary := false
    crossPaths := false
    sbtPlugin := false

    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.13",
      "org.scala-sbt" % "test-interface" % "1.0",
    )

    Compile / javacOptions ++= List("-target", "1.8", "-source", "1.8")

    // javadoc: error - invalid flag: -target.
    Compile / doc / javacOptions --= List("-target", "1.8")

    Test / publishArtifact := false

    scriptedBufferLog := false
    scriptedLaunchOpts ++= Seq(
      s"-Dplugin.version=${version.value}",
      "-Xmx256m"
    )
  })

ThisBuild / publishTo := Some(
  if(version.value.trim.endsWith("SNAPSHOT")) "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else "releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
)
ThisBuild / publishMavenStyle := true
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / homepage := Some(url("http://github.com/sbt/junit-interface/"))
ThisBuild / startYear := Some(2009)
ThisBuild / licenses += ("Two-clause BSD-style license", url("http://github.com/sbt/junit-interface/blob/master/LICENSE.txt"))
ThisBuild / developers := List(
  Developer(
    id    = "szeiger",
    name  = "Stefan Zeiger",
    email = "szeiger@novocode.com",
    url   = url("http://szeiger.de")
  )
)
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/sbt/junit-interface"),
    "scm:git@github.com:sbt/junit-interface.git"
  )
)
