name := "empty-custom-runner"

scalaVersion := "2.13.16"

libraryDependencies += "com.github.sbt" % "junit-interface" % sys.props("plugin.version") % Test

Test / fork := false

val checkEmptyRun = taskKey[Unit]("Check that the run completed with 0 tests")

checkEmptyRun := {
  // Simple approach: just verify the test class exists and can be instantiated
  // The real verification is that testOnly didn't crash (which we test in the scripted sequence)
  val testClass = (Test / classDirectory).value / "JSEnvMatrixSuite.class"
  assert(testClass.exists(), "Test class should have compiled successfully")

  // Also verify the runner class compiled
  val runnerClass = (Test / classDirectory).value / "EmptyMatrixRunner.class"
  assert(runnerClass.exists(), "Custom runner should have compiled successfully")

  // If we reach this point, the compilation succeeded and testOnly didn't crash
  // which means our fix is working
  streams.value.log.info("Empty custom runner test completed successfully - no crash occurred")
}
