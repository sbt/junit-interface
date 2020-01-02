name := "junit-interface"

organization := "com.novocode"
version := "0.12-SNAPSHOT"

autoScalaLibrary := false
crossPaths := false
sbtPlugin := false

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.13",
  "org.scala-sbt" % "test-interface" % "1.0"
)

javacOptions in Compile ++= List("-target", "1.8", "-source", "1.8")

// javadoc: error - invalid flag: -target.
javacOptions in (Compile, doc) --= List("-target", "1.8")

publishTo := Some(
  if(version.value.trim.endsWith("SNAPSHOT")) "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else "releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
)

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
description := "An implementation of sbt's test interface for JUnit 4"
homepage := Some(url("http://github.com/sbt/junit-interface/"))
startYear := Some(2009)
licenses += ("Two-clause BSD-style license", url("http://github.com/sbt/junit-interface/blob/master/LICENSE.txt"))

developers := List(
  Developer(
    id    = "szeiger",
    name  = "Stefan Zeiger",
    email = "szeiger@novocode.com",
    url   = url("http://szeiger.de")
  )
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/sbt/junit-interface"),
    "scm:git@github.com:sbt/junit-interface.git"
  )
)

// curl -X POST http://ls.implicit.ly/api/1/libraries -d 'user=szeiger&repo=junit-interface&version=0.8'

enablePlugins(SbtPlugin)
scriptedBufferLog := false
scriptedLaunchOpts ++= Seq(
  s"-Dplugin.version=${version.value}",
  "-Xmx256m"
)

resolvers += Resolver.typesafeIvyRepo("releases")
