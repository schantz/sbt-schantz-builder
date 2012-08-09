package com.schantz.sbt

import sbt._
import java.util.Date
import Keys._
import java.io.File
import scala.xml._
import com.schantz.sbt.PluginKeys._

trait SchantzBuild extends Build {
  val VERSION = "1.3"

  println("Running Schantz build Version: "+VERSION)
  
  override def projectDefinitions(baseDirectory: File) = {
    println("Project definitaions: " + baseDirectory.getAbsolutePath)
    val xmlFile = new File(baseDirectory, ".project")
    val projectName = (XML.loadFile(xmlFile) \\ "projectDescription" \ "name").text
    if(System.getProperty("root.project") == null) {
      println("Root project: " + projectName);
      System.setProperty("root.project", projectName); 
    }
    val dependencyList = EclipseBuilderPlugin.dependedProjects(baseDirectory)
    // note orders matters here as the last setting overrides the rest 
    var buildSettings = 
      // loaded before my settings, so we can override the defaults in the build
      //TestSuitesPlugin.testSuiteSettings ++ 
      ReleasePlugin.releaseSettings ++ 
      SonarPlugin.sonarSettings ++
      mySettings ++ 
      // must come after mySettings as it overrides the default directory settings
      EclipseBuilderPlugin.newSettings

    Seq(Project(projectName, file("."), settings = buildSettings) dependsOn (dependencyList: _*))
  }

  def mySettings = {
	  Defaults.defaultSettings ++ Seq(
		  Keys.javacOptions ++= javacOptions, 
      exportJars := true,
      // version and artifact name
      version <<= (baseDirectory, name in Compile) { (base, projectName) => BuildHelper.findVersionNumber(base, projectName) },
      // artifact name
		  artifactName <<= (name in Compile) { projectName => (config: String, module: ModuleID, artifact: Artifact) =>
		    var newName = projectName
        if(module.revision.nonEmpty) {
          newName += "-" + module.revision 
        }
        newName += "." + artifact.extension
        newName
		  }
    )
  }

  def javacOptions = {
    Seq("-encoding", "utf8", "-source", "1.5", "-target", "1.5")
  }
}
