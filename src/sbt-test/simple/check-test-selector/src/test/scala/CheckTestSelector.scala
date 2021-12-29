import com.novocode.junit.JUnitFramework
import com.novocode.junit.JUnitFingerprint

import org.junit.Test
import org.junit.Assert._

import sbt.testing._
import scala.collection.mutable.ArrayBuffer

/**
  * Check if TestSelector's are correctly handled by JUnitRunner.
  * Execute prepared TaskDef's using manually created instances of sbt.testing.{Framework and Runner}.
  */
class CheckTestSelector {
  val framework = new JUnitFramework();
  val runner = framework.runner(
    Array.empty[String],
    Array.empty[String],
    this.getClass().getClassLoader()
  );

  private def getEventHandler(): (ArrayBuffer[String], EventHandler) = {
    val executedItems = new scala.collection.mutable.ArrayBuffer[String]
    val eventHandler = new EventHandler {
      override def handle(event: Event) =
        if (event.status() == Status.Success) {
          executedItems.addOne(event.fullyQualifiedName())
        }
    }
    (executedItems, eventHandler)
  }

  private def getTaskDefs(selectors: Array[Selector]): Array[TaskDef] = {
    Array(
      new TaskDef("a.b.MyTestSuite", new JUnitFingerprint(), false, selectors)
    )
  }

  @Test
  def runAllViaSuiteSelector() {
    val selectors = Array[Selector](
      new SuiteSelector
    )
    val taskDefs = Array(
      new TaskDef("a.b.MyTestSuite", new JUnitFingerprint(), false, selectors)
    )

    val tasks = runner.tasks(taskDefs)
    assertEquals(tasks.size, 1)
    val task = tasks(0)

    val (executedItems, eventHandler) = getEventHandler()

    task.execute(eventHandler, Nil.toArray)
    assertArrayEquals(
      Array[Object]("a.b.MyTestSuite.testBar", "a.b.MyTestSuite.testFoo"),
      executedItems.toArray[Object]
    )
  }

  @Test
  def runAllViaTestSelectors() {
    val selectors = Array[Selector](
      new TestSelector("testFoo"),
      new TestSelector("testBar")
    )
    val taskDefs = getTaskDefs(selectors)

    val tasks = runner.tasks(taskDefs)
    assertEquals(tasks.size, 1)
    val task = tasks(0)

    val (executedItems, eventHandler) = getEventHandler()

    task.execute(eventHandler, Nil.toArray)
    assertArrayEquals(
      Array[Object]("a.b.MyTestSuite.testBar", "a.b.MyTestSuite.testFoo"),
      executedItems.toArray[Object]
    )
  }

  @Test
  def runOnlyOne() {
    val selectors = Array[Selector](
      new TestSelector("testFoo")
    )
    val taskDefs = getTaskDefs(selectors)

    val tasks = runner.tasks(taskDefs)
    assertEquals(tasks.size, 1)
    val task = tasks(0)

    val (executedItems, eventHandler) = getEventHandler()

    task.execute(eventHandler, Nil.toArray)
    assertArrayEquals(
      Array[Object]("a.b.MyTestSuite.testFoo"),
      executedItems.toArray[Object]
    )

  }
}
