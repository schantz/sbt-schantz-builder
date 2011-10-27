import org.testng.TestNG;
import java.util.*;

public class Tester {
	public static void main(String[] args) {
		List<String> suites = new ArrayList<String>();
		suites.add("testsuites/mysuite.xml");
		TestNG tester = new TestNG();
		tester.setTestSuites(suites);
		tester.run();
	}
}