package com.schantz

import java.util.Date
import sbt._
import Keys._
import java.io.File
import scala.xml._

object EclipseBuilderPlugin extends Plugin {

    
    val classpathFileName = ".classpath"
    // Settings to be included in projects that uses this plugin.
    lazy val newSettings = Seq(
        unmanagedSourceDirectories in Compile <<=  baseDirectory { base => findSourceDirectories(base / classpathFileName, base) },
        unmanagedSourceDirectories in Test <<=  baseDirectory { base => findTestSourceDirectories(base / classpathFileName, base) },
        unmanagedJars in Compile <++= baseDirectory map { dir => scanClassPath(dir) }
    )

    /*
    *   Scans the .classpath file, and finds the JarRepository.
    *   Prefixes the entries in the classpath file with the absolute path of JarRepository
    */
    def scanClassPath(basedir: File) = {
        val classpathFile = basedir / classpathFileName
        debug("Eclipe classpath file = "+classpathFile.getAbsolutePath)
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
        debug("jars = "+jars.mkString(", "))
        jars
    }

    /*
    *   Scans the .classpath file, and finds the source directories
    */
    def findSourceDirectories(classpathFile: File, basedir: File) = {
        val xml = XML.loadFile(classpathFile)
        val sourceDirs = (xml \\ "classpathentry").filter(e => (e \\ "@kind").text == "src" && (e \\ "@output").text == "").map(e => basedir / (e \\ "@path").text )
        debug("Source directories: "+sourceDirs.mkString("\n\t"))
        sourceDirs
    }
    /*
    *   Scans the .classpath file, and finds the test source directories
    */
    def findTestSourceDirectories(classpathFile: File, basedir: File) = {
        val xml = XML.loadFile(classpathFile)
        val sourceDirs = (xml \\ "classpathentry").filter(e => (e \\ "@kind").text == "src" && (e \\ "@output").text != "").map(e => basedir / (e \\ "@path").text )
        debug("Source test directories: "+sourceDirs.mkString("\n\t"))
        sourceDirs
    }

    /*
    *   finds a directory with a specified name..
    */
    def find(name: String, currentDir: File) : Array[File] = {
        debug("Searching for "+name+ " in "+currentDir.getAbsolutePath)
        if(currentDir.isDirectory) {
            val found = currentDir.listFiles.filter(f => f.getName == name)
            if(found.size > 0)
                return found
            
        }
        return find(name, new File(getParentDirectory(currentDir.getAbsolutePath)))
    }

    def getParentDirectory(dir: String) = {
        val tmp = if(dir.endsWith(""+File.separatorChar)) dir.substring(0, dir.size-1) else dir
        tmp.substring(0, tmp.lastIndexOf(File.separatorChar))
    }

    def debug(msg: String) {
        if("true".equalsIgnoreCase(System.getProperty("DEBUG")))
            println(new Date()+"\t"+msg)
    }
}