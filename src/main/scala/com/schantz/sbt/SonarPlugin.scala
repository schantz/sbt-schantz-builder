package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object SonarPlugin extends Plugin {
  val configFile = "sonar-project.properties"

  def sonarSettings = {
    inConfig(Compile)(Seq(sonarConfigTask))
  }

  private def sonarConfigTask = sonarConfig <<= (name, organization, version, unmanagedSourceDirectories, unmanagedJars, baseDirectory, classDirectory) map {
    (name, organization, version, unmanagedSourceDirectories, unmanagedJars, baseDirectory, classDirectory) =>
      var propertiesMap = Map(): Map[String, String]
      // metadata
      propertiesMap += "sonar.projectKey" -> (organization + "." + name)
      propertiesMap += "sonar.projectName" -> name
      propertiesMap += "sonar.projectVersion" -> version
      // configuration
      propertiesMap += "sonar.java.source" -> "1.5"
      propertiesMap += "sonar.java.target" -> "1.5"
      // directories
      val sourceFilter: Seq[File] => String = dirs => dirs.filter(_.exists()).foldLeft("") {
        (res, elm) => if (res == "") res + elm else res + "," + elm
      }
      // classdirectory = /home/user/workspace/project/target/scala/classes
      // basedirectory =  /home/user/workspace/project
      val targetFolder = classDirectory.getAbsolutePath().substring(baseDirectory.getAbsolutePath().length)
      // TODO add a predicate here that filters test and compile folders bases on ending (i.e. src_test)
      propertiesMap += "source" -> sourceFilter(unmanagedSourceDirectories)
      propertiesMap += "tests" -> sourceFilter(unmanagedSourceDirectories)
      propertiesMap += "binaries" -> targetFolder
      //path to project libraries (optional) libraries=junit.jar
      //propertiesMap += "libraries" -> (organization + "." + name)
  }
}