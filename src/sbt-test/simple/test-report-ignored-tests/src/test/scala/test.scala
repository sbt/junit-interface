import org.junit._
import Assert._

class TestFoo {
	
	@Test def testSuccess1(): Unit = {}
	@Test def testSuccess2(): Unit = {}

	@Test def testFailure1(): Unit = { fail("fail reason") }
	@Test def testFailure2(): Unit = { fail("fail reason") }
	@Test def testFailure3(): Unit = { fail("fail reason") }

	@Test @Ignore def testIgnored1(): Unit = { fail("unreachable code") }
	@Test @Ignore def testIgnored2(): Unit = { fail("unreachable code") }
	@Test @Ignore def testIgnored3(): Unit = { fail("unreachable code") }
	@Test @Ignore def testIgnored4(): Unit = { fail("unreachable code") }
}
