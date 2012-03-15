package com.schantz.sbt

import sbt._
import java.util.Date
import Keys._
import java.io.File
import scala.xml._
import com.schantz.sbt.PluginKeys._

trait SchantzBuild extends Build {
  override def projectDefinitions(baseDirectory: File) = {
    println("Project definitaions: " + baseDirectory.getAbsolutePath)
    val xmlFile = new File(baseDirectory, ".project")
    val projectName = (XML.loadFile(xmlFile) \\ "projectDescription" \ "name").text
    val dependencyList = EclipseBuilderPlugin.dependedProjects(baseDirectory)
    var buildSettings = mySettings ++ 
			EclipseBuilderPlugin.newSettings ++ 
			TestSuitesPlugin.testSuiteSettings ++ 
			EarPlugin.earSettings ++ 
			ReleasePlugin.releaseSettings ++ 
			SonarPlugin.sonarSettings ++
      DBBuildPlugin.dbBuildSettings

    Seq(Project(projectName, file("."), settings = buildSettings) dependsOn (dependencyList: _*))
  }

  def mySettings = {
	  Defaults.defaultSettings ++ Seq(
		  Keys.javacOptions ++= javacOptions, exportJars := true,
		  artifactName <<= (name in Compile) { projectName => (config: String, module: ModuleID, artifact: Artifact) =>
		    projectName + "-" + module.revision + "." + artifact.extension
		  }
    )
  }

  def javacOptions = {
    Seq("-encoding", "utf8", "-source", "1.5", "-target", "1.5")
  }
}
