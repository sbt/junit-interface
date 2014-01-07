name := "JUnit-Interface"

organization := "com.novocode"

version := "0.11-SNAPSHOT"

crossPaths := false

libraryDependencies += "junit" % "junit" % "4.11"

libraryDependencies += "org.scala-tools.testing" % "test-interface" % "0.5"

autoScalaLibrary := false

javacOptions in compile ++= List("-target", "1.5", "-source", "1.5")

//publishTo := Some(Resolver.file("test", file("c:/temp/repo/")))
publishTo := Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
//publishTo := Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

description := "An implementation of sbt's test interface for JUnit 4"

homepage := Some(url("http://github.com/szeiger/junit-interface/"))

startYear := Some(2009)

licenses += ("Two-clause BSD-style license", url("http://github.com/szeiger/junit-interface/blob/master/LICENSE.txt")) 

pomExtra :=
  <developers>
    <developer>
      <id>szeiger</id>
      <name>Stefan Zeiger</name>
      <timezone>+1</timezone>
      <url>http://szeiger.de</url>
    </developer>
  </developers>
  <scm>
    <url>git@github.com:szeiger/junit-interface.git/</url>
    <connection>scm:git:git@github.com:szeiger/junit-interface.git</connection>
  </scm>

// curl -X POST http://ls.implicit.ly/api/1/libraries -d 'user=szeiger&repo=junit-interface&version=0.8'
