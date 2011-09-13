package dk.mymessages

import sbt._
import Keys._
import java.io.File
import scala.xml._

object EclipseBuilderPlugin extends Plugin {
    // configuration points, like the built in `version`, `libraryDependencies`, or `compile`
    // by implementing Plugin, these are automatically imported in a user's `build.sbt`
    //val newTask = TaskKey[Unit]("new-task")
    //val newSetting = SettingKey[String]("new-setting")

    // a group of settings ready to be added to a Project
    // to automatically add them, do 
    val newSettings = Seq(
        unmanagedJars in Compile <++= baseDirectory map { dir => scanClassPath(dir) }
    )

    def scanClassPath(basedir: File) = {
        val classpathFile = basedir / ".classpath"
        println("Eclipe classpath file = "+classpathFile.getAbsolutePath)
        val jarRepos = System.getProperty("JarRepository")
        
        if(jarRepos == null) {
            val jarRepository = find("JarRepository", basedir).first.getParentFile.getAbsolutePath
            System.setProperty("JarRepository", jarRepository)
        } else {
            println("Foundation found a preconfigured system property")
        }
        val jarRepository = System.getProperty("JarRepository")

        val xml = XML.loadFile(classpathFile)
        val jars = (xml \\ "classpathentry").filter(e => (e \\ "@kind").text == "lib").map(e => Attributed.blank(new File(jarRepository, (e \\ "@path").text )))
        println("jars = "+jars)
        jars
    }

    def find(name: String, currentDir: File) : Array[File] = {
        println("Searching for "+name+ " in "+currentDir.getAbsolutePath)
        if(currentDir.isDirectory) {
            val found = currentDir.listFiles.filter(f => f.getName == name)
            if(found.size > 0)
                return found
            println("parent = "+currentDir.getParentFile)
        }
        return find(name, new File(currentDir.getParent))
    }
}