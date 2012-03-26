package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object EarPlugin extends Plugin {
  def earSettings = {
    inConfig(Compile)(Seq(earName<<= (moduleName) { (module) => module + ".ear" })) ++ 
    Seq(
      packageEar in Global <<= (baseDirectory, target, streams, earName in Compile, fullClasspath in Compile, scalaVersion) map packageEarTask
    ) 
  }

  private def packageEarTask(baseDirectory:File, target:File, streams:TaskStreams, earName:String, fullClasspath:Seq[Attributed[File]], scalaVersion:String) {
      // TODO find a more robust way of getting war file name (fx using artifact) 
      var earFile = BuildHelper.getDeployDir / earName
      var appFileDir:File = target / ("scala-" + scalaVersion)
      var appFiles = (appFileDir ** "*.jar").get x (relativeTo(appFileDir) | flat) 
      var metaInf = baseDirectory / ("ear_resources")
      var metaInfContent = (metaInf ** "*.*").get x (relativeTo(metaInf) | flat)
      var earContent = metaInfContent ++ appFiles

      streams.log.info("creating ear file: " + earFile.getPath() + " with content " + earContent.mkString("\n"))
      IO.delete(earFile)
      IO.zip(earContent, earFile)
  }
}