package com.schantz.sbt

import sbt._
import com.github.siasia.PluginKeys._
import com.github.siasia.WarPlugin._
import Keys._
import com.github.siasia.WarPlugin.packageWarTask
import _root_.sbt.Defaults.{ packageTasks, packageBinTask, inDependencies }
import Project.Initialize
import scala.xml._
import com.schantz.sbt.PluginKeys._

object MergeWebResourcesPlugin extends Plugin {
  def webSettings = {
    inConfig(Compile)(unmanagedResourceDirectories in Compile <+= (baseDirectory)(_ / "src/test/resources")) ++
      // configure web app
      com.github.siasia.WarPlugin.warSettings ++
      inConfig(Compile)(webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "src/main/webapp")) ++
      // make sure our prepare webapp custom task runs before the package war task
      inConfig(Compile)(Seq(prepareWebapp <<= com.github.siasia.WarPlugin.packageWarTask) ++
        packageTasks(packageWar, prepareWebappImpl))
  }

  private def prepareWebappImpl: Initialize[Task[Seq[(File, String)]]] = (prepareWebapp, target, excludeFilter, streams, excludeJarsFromWar, projectDependencyList) map {
    (pw, target, filter, out, jarsToRemove, dependencies) =>
      {
        val warPath = target / "webapp"
        val warLibPath = warPath / "WEB-INF/lib"
        // remove unwanted jar's
        out.log.info("Excluding jars from war: " + jarsToRemove)
        jarsToRemove.foreach(jar => IO.delete(warLibPath / jar))
        // copy web resources from other projects
        // TODO hard coded copying to figure out why style sheet looks wrong, use project dependencies when its fixed
        val deps = Seq(new java.io.File("""/Users/Lars/projects/work/sbt-spike/Advice/AdviceWeb"""), new java.io.File("""/Users/Lars/projects/work/sbt-spike/Core/FoundationWeb_2/"""))
        deps.foreach(dir => {
          val webResource = dir / "src/main/resources/webresources"
          safeCopy(webResource, warPath / "WEB-INF/classes/webresources")
        })

        (warPath).descendentsExcept("*", filter) x (relativeTo(warPath) | flat)
      }
  }

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
}