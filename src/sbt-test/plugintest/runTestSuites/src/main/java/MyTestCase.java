import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

public class MyTestCase {
	@Test
	public void testSomething() {
		assertTrue("Hi Test".contains("Test"));
	}
}