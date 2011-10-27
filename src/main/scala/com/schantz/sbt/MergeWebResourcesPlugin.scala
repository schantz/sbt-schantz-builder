package com.schantz.sbt

import sbt._
import com.github.siasia.PluginKeys._
import com.github.siasia.WarPlugin._
import Keys._
import PluginKeys._
import com.github.siasia.WarPlugin.packageWarTask
import _root_.sbt.Defaults.{packageTasks, packageBinTask, inDependencies}

class MergeWebResourcesPlugin extends Plugin {
  
  // TODO scan project dependencies for web resources to copy
  def mergeWebResourcesTask = mergeWebResources <<= (target, excludeFilter) map {
    (target, filter) =>
      {
        // copy web resources from other projects
        val warPath = target 
        val adviceWebResources = new java.io.File("""/Users/Lars/projects/work/sbt-spike/Advice/AdviceWeb/src/main/resources/webresources""")
        safeCopy(adviceWebResources, warPath / "WEB-INF/classes/webresources")

        (warPath).descendentsExcept("*", filter) x (relativeTo(warPath) | flat)
      }
  }

  // Recursively copy files from source to target, skipping any files that already exists in the target
  private def safeCopy(sourceDir: File, destDir: File) = {
    val extractRelativePath = (sourceDir.getAbsolutePath() + "(.*)").r
    val filesToCopy = recursiveListFiles(sourceDir) flatMap { file =>
      val extractRelativePath(relativePath) = file.getAbsolutePath()
      val targetPath = destDir / relativePath
      if (targetPath.exists) {
        Nil
      } else {
        Seq((file, targetPath))
      }
    }
    IO.copy(filesToCopy)
  }

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def Settings = Seq(
    // compile must have run before we can run
    mergeWebResources <<= mergeWebResources.dependsOn(packageWar),
    // ensure packageBin executes out merge web tasks prior to making the artifact
    packageBin <<= packageBin.dependsOn(mergeWebResources))
    
  // TODO clean this up
  // inConfig(DefaultConf)(warSettings0) ++
  //		addArtifact(artifact in (DefaultConf, packageWar), packageWar in DefaultConf)
          // make sure our prepare webapp custom task runs before the package war task
  //inConfig(Compile)(Seq(prepareWebappTask <<= com.github.siasia.WarPlugin.packageWarTask) ++
  //packageTasks(packageWar, prepareWebapp)) ++
}