package com.schantz
import java.util.Date
import sbt._
import Keys._
import java.io.File
import scala.xml._

trait SchantzBuild extends Build {
    
    override def projectDefinitions(baseDirectory: File) = {
        println("Project definitaions: "+baseDirectory.getAbsolutePath)
        val xmlFile = new File(baseDirectory, ".project")
        val projectName = (XML.loadFile(xmlFile) \\ "projectDescription" \ "name").text
        
        Seq(Project(projectName, file("."), settings =  mySettings ++ EclipseBuilderPlugin.newSettings ) 
        dependsOn(EclipseBuilderPlugin.dependedProjects(baseDirectory):_* ) )
    }

    def mySettings = {
        Defaults.defaultSettings ++ Seq(version := "1.0",
            exportJars := true
        )
    }
}