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
    webResourceDirectoriesInDependenciesTask ++
      // TODO this should be a setting the web apps specifies
      inConfig(Compile)(unmanagedResourceDirectories in Compile <+= (baseDirectory)(_ / "test_resources")) ++
      // configure web app
      com.github.siasia.WarPlugin.warSettings ++
      // TODO make this configurable
      inConfig(Compile)(webappResources in Compile <+= (baseDirectory in Runtime)(sd => sd / "war")) ++
      // make sure our prepare webapp custom task runs before the package war task
      inConfig(Compile)(Seq(prepareWebapp <<= com.github.siasia.WarPlugin.packageWarTask) ++
        packageTasks(packageWar, prepareWebappImpl))
  }

  private def prepareWebappImpl: Initialize[Task[Seq[(File, String)]]] = (prepareWebapp, target, excludeFilter, streams, excludeJarsFromWar, webResourceDirectoriesInDependencies) map {
    (pw, target, filter, out, jarsToRemove, webResources) =>
      {
        val warPath = target / "webapp"
        val warLibPath = warPath / "WEB-INF/lib"
        // remove unwanted jar's
        out.log.info("Excluding jars from war: " + jarsToRemove)
        jarsToRemove.foreach(jar => IO.delete(warLibPath / jar))
        
        // copy web resources from other projects
        webResources.foreach(dir => {
          safeCopy(dir, warPath / "WEB-INF/classes/webresources")
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

  // retrieve the web resource directories value in each dependent project
  private def webResourceDirectoriesInDependenciesTask = webResourceDirectoriesInDependencies <<= {
    val key: SettingKey[Seq[File]] = resourceDirectories in Compile
    val resourceDirectoriesForDependencies = Defaults.inDependencies(key, _ => Nil)

    resourceDirectoriesForDependencies map { resourceSeq =>
      val dirs = resourceSeq.flatten
      // paths are reversed as so we return then in the order they appear in the classpath
      dirs.filter(dir => (dir / "webresources").exists()).map(dir => dir / "webresources").reverse
    }
  }
}
