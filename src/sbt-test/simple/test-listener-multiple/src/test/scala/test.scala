import org.junit._

class TestFoo {
	
	@Test
	def testPass(): Unit = {
		Thread.sleep(2000)
	  import Assert._
	  assertEquals("Test should pass", true, true)
	}
	
	@Test
	def testFail(): Unit = {
		Thread.sleep(2000)
	  import Assert._
	  assertEquals("Test should fail", fail, true)
	}
}
