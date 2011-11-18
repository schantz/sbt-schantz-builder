package com.schantz.sbt

import sbt._

object PluginKeys {
  val Schantz = config("schantz")
  lazy val prepareWebapp = TaskKey[Seq[(File, String)]]("prepare-webapp")
  lazy val excludeJarsFromWar = SettingKey[Seq[String]]("exclude-jars", "jars used for compile but not packaged")
  lazy val webResourceDirectoriesInDependencies = TaskKey[Seq[File]]("web-resource-directories", "Show web resource directories for dependent projects") in Schantz
  
  lazy val packageEar = TaskKey[Unit]("package-ear", "package project into ear") in Schantz
  lazy val earName = SettingKey[String]("ear-name", "name of ear file") in Schantz

  lazy val runTestSuites = TaskKey[Unit]("run-test-suites", "runs TestNG test suites") in Schantz
  lazy val testSuites = SettingKey[Seq[String]]("test-suites", "list of test suites to run") in Schantz
  lazy val packageTest = SettingKey[Boolean]("package-test", "if true then test sources are packaged in jar") in Schantz
  
  lazy val excludeClasses = SettingKey[Seq[String]]("exclude-classes", "classes used for compile but not packaged") in Schantz
  
  // TODO figure out a way to do this nicely
  def packageTestSourcesInCompile() = {
    var isPackaged = false
    packageTest { p => isPackaged = p }
    isPackaged
  }
}