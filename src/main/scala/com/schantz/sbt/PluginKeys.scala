package com.schantz.sbt

import sbt._

object PluginKeys {
  lazy val excludeClasses = TaskKey[Unit]("exclude-classes")
  lazy val excludeJars = TaskKey[Unit]("exclude-jars")
  lazy val mergeWebResources = TaskKey[Seq[(File, String)]]("merge-webresources")
  lazy val packageEar = TaskKey[Unit]("package-ear", "package project into ear")
}