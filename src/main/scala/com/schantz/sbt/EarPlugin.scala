package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object EarPlugin extends Plugin {
  private def packageEarTask = packageEar <<= (target, streams, earName) map { (targetDir, out, name) =>
    var earFile = targetDir / name
    IO.delete(earFile)
    out.log.info("creating ear file: " + earFile.getPath())
    // TODO fetch name of all war files files
    IO.zip(Seq(), earFile)
  }

  def earSettings = {
    inConfig(Compile)(Seq(
      packageEarTask,
      // TODO name is not the correct artifact name
      earName := name + ".ear"))
  }
}

