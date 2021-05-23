name := "test-project"

scalaVersion := "2.10.7"

libraryDependencies += "com.github.sbt" % "junit-interface" % sys.props("plugin.version") % Test

testOptions += Tests.Argument(
  TestFrameworks.JUnit,
  "-v", "-n",
  "--run-listener=test.JUnitListener1",
  "--run-listener=test.JUnitListener2"
)

val listenerFile = settingKey[File]("location of the listener output")

listenerFile := target.value / "listener.txt"

javaOptions in Test += "-Djunit.output.file=" + listenerFile.value.getAbsolutePath

fork in Test := true

val checkRunListenerFile = taskKey[Unit]("Tests that the file is correct")

checkRunListenerFile := {
  val expectedContent =
    """testStarted_1 testFail(TestFoo)
      |testStarted_2 testFail(TestFoo)
      |testFailure_1 testFail(TestFoo)
      |testFailure_2 testFail(TestFoo)
      |testFinished_1 testFail(TestFoo)
      |testFinished_2 testFail(TestFoo)
      |testStarted_1 testPass(TestFoo)
      |testStarted_2 testPass(TestFoo)
      |testFinished_1 testPass(TestFoo)
      |testFinished_2 testPass(TestFoo)
      |testRunFinished_1
      |testRunFinished_2""".stripMargin.replace("\r", "")

  val actualContent = sbt.IO.readLines(listenerFile.value).mkString("\n")
  assert(
    expectedContent == actualContent,
    s"""Expecting content:
       |$expectedContent
       |Actual content:
       |$actualContent""".stripMargin
  )
}
