name := """tests-run-once"""

scalaVersion := "2.10.2"

libraryDependencies += "com.novocode" % "junit-interface" % sys.props("project.version") % "test"

fork in Test := true

testOptions += Tests.Argument(TestFrameworks.JUnit, "--verbosity=1")
