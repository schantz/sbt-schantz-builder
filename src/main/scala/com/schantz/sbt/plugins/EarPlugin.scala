package com.schantz.sbt.plugins

import sbt._
import Keys._

object EarPlugin extends Plugin {
  lazy val packageEar = TaskKey[Unit]("package-ear", "package project into ear")
  lazy val earName = SettingKey[String]("ear-name", "name of ear file")

  private def packageEarTask = packageEar <<= (target, streams, earName) map { (targetDir, out, name) =>
    var earFile = targetDir / name
    IO.delete(earFile)
    out.log.info("creating ear file: " + earFile.getPath())
    // TODO fetch name of all war files files
    //IO.zip(Seq(), earFile)
  }

  def earSettings = {
    inConfig(Compile)(Seq(
      packageEarTask,
      earName := name + ".ear"))
  }
}

