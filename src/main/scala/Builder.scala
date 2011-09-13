package dk.mymessages

import sbt._
import Keys._
import java.io.File
import scala.xml._

object EclipseBuilderPlugin extends Plugin {

    // Settings to be included in projects that uses this plugin.
    val newSettings = Seq(
        unmanagedJars in Compile <++= baseDirectory map { dir => scanClassPath(dir) }
    )

    /*
    *   Scans the .classpath file, and finds the JarRepository.
    *   Prefixes the entries in the classpath file with the absolute path of JarRepository
    */
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

    /*
    *   finds a directory with a specified name..
    */
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