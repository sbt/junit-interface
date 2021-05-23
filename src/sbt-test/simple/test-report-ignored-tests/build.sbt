name := "test-report-ignored-tests"

scalaVersion := "2.13.6"

libraryDependencies += "com.github.sbt" % "junit-interface" % sys.props("plugin.version") % "test"

val checkTestSummaryOutput = taskKey[Unit]("Check test summary output")

checkTestSummaryOutput := {
  val commandOutput = testCommandOutput(target.value, "test")
  assertContainsLine(commandOutput, "[error] Failed: Total 5, Failed 3, Errors 0, Passed 2, Ignored 4")
}

def testCommandOutput(targetRoot: File, command: String): Seq[String] = {
  val raw = IO.readLines(targetRoot / "streams" / "test" / command / "_global" / "streams" / "out")
  raw.map(filterAnsi)
}

def assertContainsLine(output: Seq[String], expectedLine: String): Unit = {
  if (!output.contains(expectedLine)) {
    throw new AssertionError(s"!!!!! SBT output doesn't contain expected line: $expectedLine")
  }
}

def filterAnsi(text: String): String =
  text.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "")

