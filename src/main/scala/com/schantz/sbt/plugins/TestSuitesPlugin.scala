package com.schantz.sbt.plugins

import sbt._
import java.util.ArrayList
import Keys._
import org.testng._

object TestSuitesPlugin extends Plugin {
  lazy val runTestSuites = TaskKey[Unit]("run-test-suites", "runs TestNG test suites")
  lazy val testSuites = SettingKey[Seq[String]]("test-suites", "list of test suites to run")

  // TODO move this tooling to a super class
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
      testSuites := Seq("testsuites/mysuite.xml"),
      libraryDependencies += "org.testng" % "testng" % "5.14"))
  }
}