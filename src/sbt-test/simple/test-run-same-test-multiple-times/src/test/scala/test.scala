import org.junit._
import Assert._

import junit.framework.{TestCase, TestSuite}
import org.junit.Assert.fail
import org.junit.runner.RunWith
import org.junit.runners.Suite.SuiteClasses

@RunWith(classOf[org.junit.runners.Suite])
@SuiteClasses(Array(
	classOf[MyActualTest],
	classOf[MyActualTest]
))
class MyCompositeTest1 extends TestSuite

@RunWith(classOf[org.junit.runners.Suite])
@SuiteClasses(Array(
	classOf[MyActualTest],
	classOf[MyActualTest]
))
class MyCompositeTest2 extends TestSuite

class MyActualTest {
	@Test def testSuccess1(): Unit = { println("print from testSuccess1") }

	@Test def testFailure1(): Unit = { println("print from testFailure1"); fail("fail reason") }
	@Test def testFailure2(): Unit = { println("print from testFailure2"); fail("fail reason") }

	@Test @Ignore def testIgnored1(): Unit = { println("print from testIgnored1"); fail("unreachable code") }
	@Test @Ignore def testIgnored2(): Unit = { println("print from testIgnored2"); fail("unreachable code") }
	@Test @Ignore def testIgnored3(): Unit = { println("print from testIgnored3"); fail("unreachable code") }
}
