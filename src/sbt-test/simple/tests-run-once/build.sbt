name := """tests-run-once"""

libraryDependencies += "com.novocode" % "junit-interface" % sys.props("project.version") % "test"

fork in Test := true

ivyLoggingLevel := UpdateLogging.Quiet
