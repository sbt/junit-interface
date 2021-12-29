package a.b

import org.junit.Test
import org.junit.Assert.assertEquals

class MyTestSuite {

  @Test
  def testFoo(): Unit = {
    assertEquals("Test should pass", true, true)
  }

  @Test
  def testBar(): Unit = {
    assertEquals("Test should pass", true, true)
  }
}
