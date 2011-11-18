package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object EarPlugin extends Plugin {
  private def packageEarTask = packageEar <<= (baseDirectory, target, streams, earName, name, version, scalaVersion, state) map {
    (base, targetDir, out, earName, name, version, scalaVersion, state) =>
      val bd = file(Project.extract(state).structure.root.toURL.getFile)
      projectDependencies map { dep =>
        dep.map { mod =>
          mod.configurations
        }
      }

      // TODO make this task depend on package war
      // TODO find a more robust way of getting war file name (fx using artifact) 
      var earFile = targetDir / earName
      var warFile = targetDir / ("scala-" + scalaVersion + "/" + name + "-" + version + ".war")
      var metaInf = base / ("src/main/application")
      var metaInfContent = (metaInf ** "*.*").get x (relativeTo(metaInf) | flat)

      out.log.info("creating ear file: " + earFile.getPath() + " using war file " + warFile.getPath())
      IO.delete(earFile)
      IO.zip(Seq(
        (warFile, warFile.getName()),
        // TODO make configurable and remove them if they do not exists
        ((base / "application.xml"), "META-INF/application.xml"),
        ((base / "war/META-INF/weblogic-application.xml"), "META-INF/weblogic-application.xml")) ++ metaInfContent, earFile)
  }

  def earSettings = {
    inConfig(Compile)(Seq(
      packageEarTask,
      earName <<= (moduleName) { (module) => module + ".ear" }))
  }
}

