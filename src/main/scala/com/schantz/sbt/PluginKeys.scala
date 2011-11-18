package com.schantz.sbt

import sbt._

object PluginKeys {
  val Schantz = config("schantz")
  lazy val warPrepare = TaskKey[Seq[(File, String)]]("war-prepare")
  lazy val warExcludedJars = SettingKey[Seq[String]]("war-exclude-jars", "jars used for compile but not packaged") in Schantz
  lazy val warExcludedMetaInfResources = SettingKey[Seq[String]]("war-exclude-metainf", "resources excluded from war META-INF") in Schantz
  lazy val warResourceDirectories = TaskKey[Seq[File]]("war-resource-directories", "Show web resource directories for dependent projects") in Schantz

  lazy val packageEar = TaskKey[Unit]("package-ear", "package project into ear") 
  lazy val earName = SettingKey[String]("ear-name", "name of ear file") in Schantz

  lazy val runTestSuites = TaskKey[Unit]("run-test-suites", "runs TestNG test suites") in Schantz
  lazy val testSuites = SettingKey[Seq[String]]("test-suites", "list of test suites to run") in Schantz
  lazy val packageTest = SettingKey[Boolean]("package-test", "if true then test sources are packaged in jar") in Schantz

  lazy val releaseInfo = TaskKey[Unit]("release-info", "Show release information such as branches and repositories") in Schantz

  lazy val jarExcludedClasses = SettingKey[Seq[String]]("exclude-classes", "classes used for compile but not packaged") in Schantz

  // TODO figure out a way to do this nicely
  def packageTestSourcesInCompile() = {
    var isPackaged = false
    packageTest { p => isPackaged = p }
    isPackaged
  }
}