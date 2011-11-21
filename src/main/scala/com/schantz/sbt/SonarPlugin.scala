package com.schantz.sbt

import sbt._
import Keys._

object SonarPlugin extends Plugin {
  val configFile = "sonar-project.properties"
  lazy val sonarConfig = TaskKey[Unit]("sonar-config")

  private def configureSonarTask = sonarConfig <<= (name, organization, version, unmanagedSourceDirectories, unmanagedJars) map {
    (name, organization, version, unmanagedSourceDirectories, unmangedJars) =>
      var propertiesMap = Map(): Map[String, String]
      // metadata
      propertiesMap += "sonar.projectKey" -> (organization + "." + name)
      propertiesMap += "sonar.projectName" -> name
      propertiesMap += "sonar.projectVersion" -> version
      // directories
      val sourceFilter: Seq[File] => String = dirs => dirs.filter(_.exists()).foldLeft("") {
        (res, elm) => if (res == "") res + elm else res + "," + elm
      }
      // TODO add a predicate here that filters test and compile folders bases on ending (i.e. src_test)
      propertiesMap += "source" -> sourceFilter(unmanagedSourceDirectories)
      propertiesMap += "tests" -> sourceFilter(unmanagedSourceDirectories)
      propertiesMap += "binaries" -> "bin"
      propertiesMap += "libraries" -> (organization + "." + name)
      propertiesMap += "" -> (organization + "." + name)
      ()
  }

  /*
# path to project libraries (optional)
# libraries=junit.jar
 
#Uncomment those lines if some features of java 5 or java 6 like annotations, enum, ... 
#are used in the source code to be analysed
sonar.java.source=1.5
#sonar.java.target=1.5 
*/

}