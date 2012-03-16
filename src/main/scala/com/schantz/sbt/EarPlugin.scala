package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object EarPlugin extends Plugin {
  def earSettings = {
    inConfig(Compile)(Seq(earName<<= (moduleName) { (module) => module + ".ear" })) ++ 
    Seq(
      packageEar in Global <<= (baseDirectory, target, streams, earName in Compile, name, version, scalaVersion) map packageEarTask
    ) 
  }

  private def packageEarTask(baseDirectory:File, target:File, streams:TaskStreams, earName:String, name:String, version:String, scalaVersion:String) {
      // TODO find a more robust way of getting war file name (fx using artifact) 
      var earFile = target / earName
      var warFile = target / ("scala-" + scalaVersion + "/" + name + "-" + version + ".war")
      var metaInf = baseDirectory / ("src/main/application")
      var metaInfContent = (metaInf ** "*.*").get x (relativeTo(metaInf) | flat)

      streams.log.info("creating ear file: " + earFile.getPath() + " using war file " + warFile.getPath())
      IO.delete(earFile)
      IO.zip(Seq(
        (warFile, warFile.getName()),
        // TODO make configurable and remove them if they do not exists
        ((baseDirectory / "application.xml"), "META-INF/application.xml"),
        ((baseDirectory / "war/META-INF/weblogic-application.xml"), "META-INF/weblogic-application.xml")) ++ metaInfContent, earFile)
  }
}