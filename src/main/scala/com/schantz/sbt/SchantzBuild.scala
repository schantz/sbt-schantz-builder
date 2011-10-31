package com.schantz.sbt

import sbt._
import java.util.Date
import Keys._
import java.io.File
import scala.xml._
import com.schantz.sbt.plugins._

trait SchantzBuild extends Build {
  override def projectDefinitions(baseDirectory: File) = {
    println("Project definitaions: " + baseDirectory.getAbsolutePath)
    val xmlFile = new File(baseDirectory, ".project")
    val projectName = (XML.loadFile(xmlFile) \\ "projectDescription" \ "name").text
    var buildSettings = mySettings ++ EclipseBuilderPlugin.newSettings ++ TestSuitesPlugin.testSuiteSettings ++ EarPlugin.earSettings
    val projectDependencies = EclipseBuilderPlugin.dependedProjects(baseDirectory)

    val warDir = baseDirectory / "src/main/webapp"
    if (warDir.exists()) {
      println("====== FOUND WEBAPP: " + projectName + " ======")
      buildSettings ++= MergeWebResourcesPlugin.webSettings
      // TODO merge depended projects here instead of in separate task
    }

    Seq(Project(projectName, file("."), settings = buildSettings) dependsOn (projectDependencies: _*))
  }

  def mySettings = {
    Defaults.defaultSettings ++ Seq(version := "1.0",
      exportJars := true)
  }

  def javacOptions = Keys.javacOptions
}