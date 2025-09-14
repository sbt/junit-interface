import org.junit.runner.{Description, Runner}
import org.junit.runner.notification.RunNotifier

// Simulates a parameterized runner that has no atomic tests at discovery time.
// getDescription returns a root Description with zero children.
final class EmptyMatrixRunner(cls: Class[_]) extends Runner {
  private val root: Description = Description.createSuiteDescription(cls)

  override def getDescription(): Description = root

  // Nothing to run
  override def run(notifier: RunNotifier): Unit = ()
}