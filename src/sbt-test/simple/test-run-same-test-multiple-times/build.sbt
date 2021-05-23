name := "test-report-ignored-tests"

scalaVersion := "2.13.6"

libraryDependencies += "com.github.sbt" % "junit-interface" % sys.props("plugin.version") % "test"

// NOTE: by default junit-interface ignores `org.junit.runners.Suite`,
// which is used in this integration test,
// we need to disable this behaviour
Test / testOptions += Tests.Argument("--ignore-runners="/*, "--verbosity=3"*/)

val checkAfterTestOnly = taskKey[Unit]("checkAfterTestOnly")
val checkAfterTest = taskKey[Unit]("checkAfterTest")

checkAfterTestOnly := {
  val commandOutput = testCommandOutput(target.value, "testOnly")
  // contains 2x more tests that single `MyActualTest`
  assertContainsLine(commandOutput, "[error] Failed: Total 6, Failed 4, Errors 0, Passed 2, Ignored 6")
}

checkAfterTest := {
  val commandOutput = testCommandOutput(target.value, "test")
  // MyCompositeTest1 (includes 2x MyActualTests) + MyCompositeTest2  (includes 2x MyActualTests) + MyActualTests itself
  assertContainsLine(commandOutput, "[error] Failed: Total 15, Failed 10, Errors 0, Passed 5, Ignored 15")
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
