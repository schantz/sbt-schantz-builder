package com.schantz.sbt

import sbt._

object PluginKeys {
  // tooling
  lazy val sonarConfig = TaskKey[Unit]("schantz-sonar-config", "Build sonar config file for projects")
  
  // war
  lazy val warExcludedJars = SettingKey[Seq[String]]("schantz-war-exclude-jars", "jars used for compile but not packaged") 
  lazy val warExcludedMetaInfResources = SettingKey[Seq[String]]("schantz-war-exclude-metainf", "resources excluded from war META-INF") 
  lazy val warResourceDirectories = TaskKey[Seq[File]]("schantz-war-resource-directories", "Show web resource directories for dependent projects") 

  // ear
  lazy val packageEar = TaskKey[Unit]("schantz-package-ear", "package project into ear") 
  lazy val earName = SettingKey[String]("schantz-ear-name", "name of ear file") 

  // test
  lazy val runTestSuites = TaskKey[Unit]("schantz-run-test-suites", "runs TestNG test suites") 
  lazy val testSuites = SettingKey[Seq[String]]("schantz-test-suites", "list of test suites to run") 

  // release
  lazy val releaseInfo = TaskKey[Unit]("schantz-release-info", "Show release information such as branches and repositories")

  // package
  lazy val packageExcludedClasses = SettingKey[Seq[String]]("schantz-exclude-classes", "classes used for compile but not packaged")

  // DB build
  lazy val dbBuild = TaskKey[Unit]("schantz-dbbuild", "Build a test database from a anonomous prod database") 
  lazy val dbBuildClass = SettingKey[String]("schantz-dbbuild-class", "Full name of class used to build database") 
  lazy val dbBuildName = SettingKey[String]("schantz-dbbuild-name", "Name of generated database") 
  lazy val dbBuildPath = SettingKey[String]("schantz-dbbuild-path", "Path of the full database to build test database from") 

  // TMC
  lazy val tmcArchive = TaskKey[Unit]("schantz-archive-tmc", "archive to TMC") 
}