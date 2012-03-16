import com.schantz.sbt.TestSuitesPlugin._

Seq(testSuiteSettings :_*) 

testSuites in Compile := Seq("testsuites/suite.xml")
