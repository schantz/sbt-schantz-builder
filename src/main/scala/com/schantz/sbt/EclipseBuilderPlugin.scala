package com.schantz.sbt

import java.util.Date
import sbt._
import Keys._
import java.io.File
import scala.util.matching.Regex
import scala.xml._
import com.schantz.sbt.PluginKeys._

object EclipseBuilderPlugin extends Plugin {
  val classpathFileName = ".classpath"

  // Settings to be included in projects that uses this plugin.
  lazy val newSettings = {
    Seq(
      // version and artifact name
      version <<= (baseDirectory) { (base) => findVersionNumber(base) },
      // resources
      unmanagedResourceDirectories in Compile <<= baseDirectory { base => findResourceDirectories(base) },
      // source directories
      unmanagedSourceDirectories in Compile <<= baseDirectory { base => findSourceDirectories(base / classpathFileName, base) },
      // we must add test sources to compile as we have cross project test dependencies
      unmanagedSourceDirectories in Compile <++= baseDirectory { base => findTestSourceDirectories(base / classpathFileName, base) },
      // dependencies
      unmanagedJars in Compile <++= baseDirectory map { base => scanClassPath(base) },
      // classes to exclude
      mappings in (Compile, packageBin) ~= filterClassesFromPackage)
  }

  /*
   * Excludes classes from being packaged
   *
   * fx. "javax/servlet/Servlet.class"
   */
  def filterClassesFromPackage(mappings: Seq[(File, String)]) = {
    mappings filter {
      case (file, toPath) => {
        // TODO try testing with something in foundation and see if its removed, if it is then add key
        toPath != "javax/servlet/Servlet.class"
      }
    }
  }

  /*
    *   Scans the .classpath file, and finds the JarRepository.
    *   Prefixes the entries in the classpath file with the absolute 
    *   path of JarRepository
    */
  def scanClassPath(basedir: File) = {
    val classpathFile = basedir / classpathFileName
    debug("Eclipe classpath file = " + classpathFile.getAbsolutePath)
    val jarRepos = System.getProperty("JarRepository")

    if (jarRepos == null) {
      val jarRepository = find("JarRepository", basedir).first.getParentFile.getAbsolutePath
      System.setProperty("JarRepository", jarRepository)
    } else {
      println("Foundation found a preconfigured system property")
    }
    val jarRepository = System.getProperty("JarRepository")

    val xml = XML.loadFile(classpathFile)
    val jars = (xml \\ "classpathentry").filter(e => (e \\ "@kind").text == "lib").map(e => Attributed.blank(new File(jarRepository, (e \\ "@path").text)))
    debug("jars = " + jars.mkString(", "))
    jars
  }

  /*
    *   Scans the .classpath file, and finds the source directories
    */
  def findSourceDirectories(classpathFile: File, basedir: File) = {
    val xml = XML.loadFile(classpathFile)
    val sourceDirs = (xml \\ "classpathentry").filter(e => (e \\ "@kind").text == "src" && (e \\ "@output").text == "").map(e => basedir / (e \\ "@path").text)
    debug("Source directories: " + sourceDirs.mkString("\n\t"))
    sourceDirs
  }

  /* 
   * Scan base directory for version info
   */
  def findVersionNumber(basedir: File): String = {
    import scala.io._
    val versionFiles = Seq((basedir / "resources/build.version"), (basedir / "src/main/resources/build.version")).filter(_.exists())

    var majorVersion = "1."
    var minorVersion = "0"

    versionFiles.foreach { file =>
      debug("Scanning version file: " + file.getAbsoluteFile())
      val versionInfo = Source.fromFile(file).getLines
      val minorNumberRegex = """build.number=(.*)""".r
      val majorNumberRegex = """major.version=.*-(.*)""".r

      for (line <- versionInfo) {
        line match {
          case minorNumberRegex(minor) => minorVersion = minor
          case majorNumberRegex(major) => majorVersion = major
          case _ => ()
        }
      }
    }
    val versionNumber = majorVersion + minorVersion
    debug("Setting version: " + versionNumber + " for " + basedir.getAbsolutePath())
    versionNumber
  }

  /**
   * Scans base directory for resource folders
   */
  def findResourceDirectories(basedir: File) = {
    var resourceFilter: File => Boolean = content => {
      if (!content.isDirectory())
        false
      var path = content.getAbsolutePath()
      (path.matches(".*/resources") || path.matches(""".*WebContent"""))
    }
    basedir.listFiles().filter(resourceFilter)
  }

  /*
    *   Scans the .classpath file, and finds the test source directories
    */
  def findTestSourceDirectories(classpathFile: File, basedir: File) = {
    val xml = XML.loadFile(classpathFile)
    val sourceDirs = (xml \\ "classpathentry").filter(e => (e \\ "@kind").text == "src" && (e \\ "@output").text != "").map(e => basedir / (e \\ "@path").text)
    debug("Source test directories: " + sourceDirs.mkString("\n\t"))
    sourceDirs
  }

  /*
    *   Scans the .classpath file, and finds the project dependencies
    */
  def findProjects(classpathFile: File, basedir: File) = {
    val xml = XML.loadFile(classpathFile)
    val sourceDirs = (xml \\ "classpathentry").filter(e => {
      val pathText = (e \\ "@path").text
      (e \\ "@kind").text == "src" && (pathText != "" && pathText.startsWith("/") && !pathText.endsWith(".jar"))
    }).map(e => {
      val searchText = (e \\ "@path").text.replaceFirst("/", "")
      println("Trying to find: " + searchText)
      find(searchText, basedir)
    })
    debug("Project dependencies: " + sourceDirs.mkString("\n\t"))
    sourceDirs
  }

  def dependedProjects(basedir: File) = {
    // add % "test->test" to allow for project inter dependencies during test
    val projects = findProjects(new File(basedir, classpathFileName), basedir).map(p => ClasspathDependency(RootProject(p.first), None))
    debug("Project dependencies: " + projects)
    projects
  }

  /*
    *   finds a directory with a specified name..
    */
  def find(name: String, currentDir: File): Option[File] = {
    var parentPath = new File(getParentDirectory(currentDir.getAbsolutePath))
    var notFound = true
    while (parentPath.getAbsolutePath != "/") {
      debug("Searching: " + parentPath.getAbsolutePath)
      val found = searchDown(parentPath, name)
      if (found.size > 0) {
        return Some(found.first)
      }

      parentPath = new File(getParentDirectory(parentPath.getAbsolutePath))
    }
    None
  }

  def searchDown(aStartingDir: File, name: String): List[File] = {
    var result = List[File]()
    val filesAndDirs = aStartingDir.listFiles()

    for (file <- filesAndDirs) {
      if (file.getName == ".project") {
        val projectName = (XML.loadFile(file) \\ "projectDescription" \ "name").text
        if (projectName == name)
          result = new File(getParentDirectory(file.getAbsolutePath)) :: result
      }

      if (file.isDirectory()) {
        val deeperList = searchDown(file, name)
        result = deeperList ::: result
      }
    }
    return result;
  }

  def getParentDirectory(dir: String) = {
    val tmp = if (dir.endsWith("" + File.separatorChar)) dir.substring(0, dir.size - 1) else dir
    tmp.substring(0, tmp.lastIndexOf(File.separatorChar))
  }

  def debug(msg: String) {
    if ("true".equalsIgnoreCase(System.getProperty("DEBUG")))
      println(new Date() + "\t" + msg)
  }
}
