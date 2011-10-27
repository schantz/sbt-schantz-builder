import com.schantz.sbt.RunTestSuitesPlugin._

Seq(testSuiteSettings :_*)

// the target needs testng on the classpath
libraryDependencies += "org.testng" % "testng" % "5.14"

libraryDependencies += "de.johoop" % "sbt-testng-interface" % "1.0.0" % "test"

//testFrameworks += new TestFramework("de.johoop.testng.TestNGFramework")

//testOptions <+= (crossTarget, resourceDirectory in Test) map { (target, testResources) => 
//  Tests.Argument(
//    "-d", (target / "testng").absolutePath, 
//   (testResources / "testsuites/mysuite.xml").absolutePath)
//}