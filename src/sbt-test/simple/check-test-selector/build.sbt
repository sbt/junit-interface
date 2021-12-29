name := "test-project"

scalaVersion := "2.13.7"

libraryDependencies += "com.github.sbt" % "junit-interface" % sys.props("plugin.version") % "test"
libraryDependencies += "org.scala-sbt" % "test-agent" % "1.5.5" % Test
