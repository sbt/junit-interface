import org.junit._

class TestFoo {
	
	@Test
	def testFoo(): Unit = {
	  import Assert._
	  assertEquals("Test should pass", true, true)
	}
}