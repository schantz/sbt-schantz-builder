package com.schantz.sbt

import sbt._
import com.schantz.sbt.PluginKeys._
import Project.Initialize
import Keys._

object CleanPlugin extends Plugin {
  def cleanPluginSettings = {
    Seq(cleanPluginInDependenciesTask)
  }

  private def cleanPluginInDependenciesTask = cleanPluginInDependencies in Compile <<= {
    // ref points to current project, so define a function that given a ref executes a command on it 
    val getBaseDirectory: ProjectRef => Initialize[Option[File]] = ref => (baseDirectory in ref).?
    val baseDirectoryForDependencies = Defaults.forDependencies(getBaseDirectory)

    baseDirectoryForDependencies map { baseDirSeq =>
      val dirs = baseDirSeq.flatten
      dirs.map { dir =>
        var pluginDir = dir / "project/lib_managed/jars/com.schantz"
        if (pluginDir.exists()) {
          println("cleaning plugin dir " + pluginDir.getAbsolutePath())
          IO.delete(pluginDir)
        } else {
          println(pluginDir.getAbsolutePath() + " does not exists")
        }
      }
    }
  }
}