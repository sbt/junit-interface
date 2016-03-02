// Copyright (C) 2015 Sam Halliday
// License: Apache-2.0

import sbt._
import Keys._

object SonatypeSupport {
  val GPL3 = ("GPL 3.0" -> url("http://www.gnu.org/licenses/gpl.html"))
  val LGPL3 = ("LGPL 3.0" -> url("http://www.gnu.org/licenses/lgpl.html"))
  val Apache2 = ("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
  val BSD2 = ("BSD-2-Clause" -> url("https://opensource.org/licenses/BSD-2-Clause"))

  def sonatype(
    ghUser: String,
    ghRepo: String,
    license: (String, URL)
  ) = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    homepage := Some(url(s"http://github.com/$ghUser/$ghRepo")),
    licenses := Seq(license),
    publishTo <<= version { v: String =>
      val nexus = "https://oss.sonatype.org/"
      if (v.contains("SNAP")) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    credentials ++= {
      for {
        username <- sys.env.get("SONATYPE_USERNAME")
        password <- sys.env.get("SONATYPE_PASSWORD")
      } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)
    }.toSeq,
    pomExtra := (
      <scm>
        <url>git@github.com:${ ghUser }/${ ghRepo }.git</url>
        <connection>scm:git:git@github.com:${ ghUser }/${ ghRepo }.git</connection>
      </scm>
      <developers>
        <developer>
          <id>$ghUser</id>
        </developer>
      </developers>
    )
  )
}
