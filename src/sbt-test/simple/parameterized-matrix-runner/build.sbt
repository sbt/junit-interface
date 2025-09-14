name := "parameterized-matrix-runner"

scalaVersion := "3.3.6"

libraryDependencies += "com.github.sbt" % "junit-interface" % sys.props("plugin.version") % Test

Test / fork := false

val checkMatrixRun = taskKey[Unit]("Check that the parameterized matrix ran correctly")

checkMatrixRun := {
  // Verify the matrix output file was created with expected content
  val matrixOutput = target.value / "matrix-results.txt"
  assert(matrixOutput.exists(), "Matrix results file should have been created")

  val results = IO.readLines(matrixOutput).toSet
  val expected = Set(
    "test-env1-input1",
    "test-env1-input2",
    "test-env2-input1",
    "test-env2-input2"
  )

  assert(results == expected, s"Expected matrix results $expected, but got $results")
  streams.value.log.info(s"Parameterized matrix test ran successfully with ${results.size} combinations")
}