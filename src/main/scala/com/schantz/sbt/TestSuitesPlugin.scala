package com.schantz.sbt

import sbt._
import java.util.ArrayList
import Keys._
import org.testng._
import com.schantz.sbt.PluginKeys._

object TestSuitesPlugin extends Plugin {
  // TODO move this implicit to a util class
  class JavaListWrapper[T](val seq: Seq[T]) {
    def toJavaList = seq.foldLeft(new java.util.ArrayList[T](seq.size)) { (al, e) => al.add(e); al }
  }
  implicit def listToJavaList[T](l: Seq[T]) = new JavaListWrapper(l)

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
    tester.setTestSuites(suites.toJavaList)
    tester.run()
  }
}

