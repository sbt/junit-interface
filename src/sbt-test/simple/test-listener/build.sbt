name := "test-project"

scalaVersion := "2.10.7"

libraryDependencies += "com.github.sbt" % "junit-interface" % sys.props("plugin.version") % "test"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-n", "--run-listener=test.JUnitListener")

val listenerFile = settingKey[File]("location of the listener output")

listenerFile := (target.value / "listener.txt")

javaOptions in Test += "-Djunit.output.file=" + listenerFile.value.getAbsolutePath

fork in Test := true

val checkRunListenerFile = taskKey[Unit]("Tests that the file is correct")

checkRunListenerFile := {
  val expectedContents = List("testRunStarted",
                "testStarted testFail(TestFoo)",
                "testFailure testFail(TestFoo)",
                "testFinished testFail(TestFoo)",
                "testStarted testPass(TestFoo)",
                "testFinished testPass(TestFoo)",
                "testRunFinished")
  val contents = sbt.IO.readLines(listenerFile.value)
  assert(expectedContents == contents, "Expecting contents=" + expectedContents + " actual=" + contents)
}
