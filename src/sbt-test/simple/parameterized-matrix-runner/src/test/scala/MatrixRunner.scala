import org.junit.runner.{Description, Runner}
import org.junit.runner.notification.RunNotifier
import org.junit.Test
import java.io.{File, FileWriter}

// Simulates a parameterized test runner that dynamically generates test combinations
final class MatrixRunner(testClass: Class[_]) extends Runner {
  private val environments = List("env1", "env2")
  private val inputs = List("input1", "input2")

  private val root = Description.createSuiteDescription(testClass)

  // Generate matrix of test descriptions
  private val testCombinations = for {
    env <- environments
    input <- inputs
  } yield {
    val testDesc = Description.createTestDescription(testClass, s"test-$env-$input")
    root.addChild(testDesc)
    (env, input, testDesc)
  }

  override def getDescription(): Description = root

  override def run(notifier: RunNotifier): Unit = {
    notifier.fireTestRunStarted(root)

    testCombinations.foreach { case (env, input, desc) =>
      notifier.fireTestStarted(desc)
      try {
        // Simulate test execution - write result to file
        val outputFile = new File("target/matrix-results.txt")
        outputFile.getParentFile.mkdirs()
        val writer = new FileWriter(outputFile, true) // append mode
        try {
          writer.write(s"test-$env-$input\n")
        } finally {
          writer.close()
        }

        notifier.fireTestFinished(desc)
      } catch {
        case e: Exception =>
          notifier.fireTestFailure(new org.junit.runner.notification.Failure(desc, e))
      }
    }

    notifier.fireTestRunFinished(new org.junit.runner.Result())
  }
}
