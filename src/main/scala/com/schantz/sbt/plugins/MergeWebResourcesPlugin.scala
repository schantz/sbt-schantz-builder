package com.schantz.sbt.plugins

import sbt._
import com.github.siasia.PluginKeys._
import com.github.siasia.WarPlugin._
import Keys._
import com.github.siasia.WarPlugin.packageWarTask
import _root_.sbt.Defaults.{ packageTasks, packageBinTask, inDependencies }
import Project.Initialize
import scala.xml._

object MergeWebResourcesPlugin extends Plugin {
  lazy val prepareWebappTask = TaskKey[Seq[(File, String)]]("prepare-webapp")

  def webSettings = {
    // TODO try readding these
    inConfig(Compile)(unmanagedResourceDirectories in Compile <+= (baseDirectory)(_ / "src/test/resources")) ++
      // configure web app
      com.github.siasia.WarPlugin.warSettings ++
      inConfig(Compile)(webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "src/main/webapp")) ++
      // make sure our prepare webapp custom task runs before the package war task
      // TODO replace this ref with the task from the plugin
      inConfig(Compile)(Seq(prepareWebappTask <<= com.github.siasia.WarPlugin.packageWarTask) ++
        packageTasks(packageWar, prepareWebapp))
  }

  private def prepareWebapp: Initialize[Task[Seq[(File, String)]]] = (prepareWebappTask, target, excludeFilter) map {
    (pw, target, filter) =>
      {
        val warPath = target / "webapp"
        val warLibPath = warPath / "WEB-INF/lib"
        // remove unwanted jar's
        val jarsToRemove = List("servlet-api.jar", "gwt-user.jar", "gwt-dev.jar")
        jarsToRemove.foreach(jar => IO.delete(warLibPath / jar))
        // copy web resources from other projects
        // TODO scan project dependencies for webresources to copy
        val adviceWebResources = new java.io.File("""/Users/Lars/projects/work/sbt-spike/Advice/AdviceWeb/src/main/resources/webresources""")
        safeCopy(adviceWebResources, warPath / "WEB-INF/classes/webresources")

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

  // TODO move this to eclipse plugin and remove the from unmanagedJars instead
  def getJarsToRemove(baseDir: File) = {
    val configFile = baseDir / "project/config.xml"
    if (configFile.exists()) {
      val xml = XML.loadFile(configFile)
      val jarsToExclude = (xml \\ "exclude-jars").map(e => (e \\ "@matches").text)
      jarsToExclude
    }
    Nil
  }
}