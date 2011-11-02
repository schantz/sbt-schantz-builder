package com.schantz.sbt

import sbt._

object PluginKeys {
  lazy val prepareWebapp = TaskKey[Seq[(File, String)]]("prepare-webapp")
  lazy val excludeJarsFromWar = SettingKey[Seq[String]]("exclude-jars", "jars used for compile but not packaged")
  
  lazy val packageEar = TaskKey[Unit]("package-ear", "package project into ear")
  lazy val earName = SettingKey[String]("ear-name", "name of ear file")

  lazy val runTestSuites = TaskKey[Unit]("run-test-suites", "runs TestNG test suites")
  lazy val testSuites = SettingKey[Seq[String]]("test-suites", "list of test suites to run")
  lazy val packageTest = SettingKey[Boolean]("package-test", "if true then test sources are packaged in jar")
  
  lazy val excludeClasses = SettingKey[Seq[String]]("exclude-classes", "classes used for compile but not packaged")
  lazy val projectDependencyList = SettingKey[Seq[sbt.ClasspathDependency]]("project-dependency-list")
  
  // TODO figure out a way to do this nicely
  def packageTestSourcesInCompile() = {
    var isPackaged = false
    packageTest { p => isPackaged = p }
    isPackaged
  }
}