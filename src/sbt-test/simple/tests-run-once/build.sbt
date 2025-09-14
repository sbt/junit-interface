name := """tests-run-once"""

scalaVersion := "2.11.12"

libraryDependencies += "com.github.sbt" % "junit-interface" % sys.props("plugin.version") % "test"

fork in Test := true

testOptions += Tests.Argument(TestFrameworks.JUnit, "--verbosity=1")
