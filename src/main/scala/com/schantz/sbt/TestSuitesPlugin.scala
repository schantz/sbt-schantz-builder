package com.schantz.sbt

import sbt._
import java.util.ArrayList
import Keys._
import org.testng._
import com.schantz.sbt.PluginKeys._
import scala.collection.JavaConversions._

object TestSuitesPlugin extends Plugin {

  def testSuiteSettings = {
    inConfig(Compile)(Seq(
      runTestSuitesTask,
      testSuites := Seq("testsuites/mysuite.xml")))
  }

  private def runTestSuitesTask = runTestSuites <<= (target, streams, testSuites) map {
    (target, streams, testSuites) =>
      import streams.log
      log.info("running test suites: " + testSuites)
      runSuites(testSuites)
  }

  private def runSuites(suites: Seq[String]) = {
    var tester = new TestNG
    tester.setTestSuites(suites)
    tester.run()
  }
}