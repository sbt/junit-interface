import org.junit._

class TestFoo {
	
	@Test
	def testPass(): Unit = {
	  import Assert._
	  assertEquals("Test should pass", true, true)
	}
	
	@Test
	def testFail(): Unit = {
	  import Assert._
	  assertEquals("Test should fail", fail, true)
	}
}
