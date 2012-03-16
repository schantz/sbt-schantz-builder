package com.schantz.sbt

import sbt._
import java.util.ArrayList
import Keys._
import org.testng._
import com.schantz.sbt.PluginKeys._
import scala.collection.JavaConversions._

object TestSuitesPlugin extends Plugin {

  def testSuiteSettings = {
    inConfig(Compile)(Seq(testSuites := Seq("testsuites/mysuite.xml"))) ++ Seq(
      runTestSuites <<= (target, streams, testSuites in Compile) map runTestSuitesTask
    )
  }

  private def runTestSuitesTask(target:File, streams:TaskStreams, testSuites:Seq[String]) {
      streams.log.info("running test suites: " + testSuites)
      runSuites(testSuites)
  }

  private def runSuites(suites: Seq[String]) = {
    var tester = new TestNG
    tester.setTestSuites(suites)
    tester.run()
  }
}

