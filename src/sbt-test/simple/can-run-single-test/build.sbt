name := "test-project"

libraryDependencies += "com.novocode" % "junit-interface" % sys.props("project.version") % "test"

val checkTestDefinitions = taskKey[Unit]("Tests that the test is discovered properly")

checkTestDefinitions := {
  val definitions = (definedTests in Test).value
  assert(definitions.length == 1, "Found more than the one test!")
  // TODO - Check fingerprint?
  streams.value.log.info("Test name = " + definitions.head.name)
  assert(definitions.head.name == "TestFoo", "Failed to discover/name the unit test!")
}

ivyLoggingLevel := UpdateLogging.Quiet
