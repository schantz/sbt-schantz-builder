package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object EarPlugin extends Plugin {
  private def packageEarTask = packageEar <<= (baseDirectory, target, streams, earName, scalaVersion, moduleName, version) map {
    (base, targetDir, out, name, scala, module, ver) =>
      // TODO find a more robust way of getting war file name (fx using artifact) 
      var earFile = targetDir / name
      var warFile = targetDir / ("scala-" + scala + "/" + module + "_" + scala + "-" + ver + ".war")
      var metaInf = base / ("src/main/application")
      var metaInfContent = (metaInf ** "*.*").get x (relativeTo(metaInf) | flat)

      out.log.info("creating ear file: " + earFile.getPath() + " using war file " + warFile.getPath())
      IO.delete(earFile)
      IO.zip(Seq((warFile, warFile.getName())) ++ metaInfContent, earFile)
  }

  def earSettings = {
    inConfig(Compile)(Seq(
      packageEarTask,
      earName <<= (moduleName, version) { (m, v) => m + "-" + v + ".ear" }))
  }
}

