import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.BlockJUnit4ClassRunner;


public class MyTest extends SuperclassTests  {
  private static int testRuns = 0;

  @Test
  public void simpleCheck() {
    System.out.println("hello" + testRuns);
    testRuns++;
    Assert.assertEquals(1, testRuns);
  }
  
}
