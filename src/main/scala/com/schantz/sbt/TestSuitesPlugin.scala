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

  private def runTestSuitesTask = runTestSuites <<= (target, streams, testSuites) map {
    (targetDirectory, taskStream, suites) =>
      import taskStream.log
      log.info("running test suites: " + suites)
      runSuites(suites)
  }

  private def runSuites(testSuites: Seq[String]) = {
    var tester = new TestNG
    tester.setTestSuites(testSuites.toJavaList)
    tester.run()
  }

  def testSuiteSettings = {
    inConfig(Compile)(Seq(
      runTestSuitesTask,
      testSuites := Seq("testsuites/mysuite.xml")))
  }
}