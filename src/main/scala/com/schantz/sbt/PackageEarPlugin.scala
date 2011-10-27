package com.schantz.sbt

import sbt._
import PluginKeys._
import Keys._

object EarPlugin extends Plugin {
  def packageEarTask = packageEar <<= (target, streams) map { (targetDirectory, taskStream) =>
    import taskStream.log
    log.error("unable to find war file for ear: ")
    error("")
    // TODO get war file and use package artifact to make it
  }

  // TODO add this after war plugin war plugins has run
  
  // nest our plugin settings inside a object to ensure it does not clash with other plugins
  object ear {
    // inject name and description of setting into the configuration 
    // sbt> package-ear
    val earName = SettingKey[String]("ear-name", "name of ear file")

    // allow ear file to be a configured setting
    // TODO name it as default project (artifact.ear)
    lazy val settings: Seq[Setting[_]] = Seq(earName := "my.ear")

    // TODO get name of war file from package-war  
    //s.log.debug("Writing to " + versionFile + ":\n   " + versionFileContents.mkString("\n   "))

    // for making ear
    var ear = TaskKey[Set[File]]("package-ear") <<= (unmanagedBase, streams) map { (libs, out) =>
      val jme = url("http://jmonkeyengine.com/nightly/jME3_2011-06-12.zip")
      val target = libs / "jme"
      IO.delete(target)
      IO.createDirectory(target)
      out.log.info("Fetching jME3 from " + jme)
      IO.unzipURL(jme, target, AllPassFilter)
    }
  }
}

