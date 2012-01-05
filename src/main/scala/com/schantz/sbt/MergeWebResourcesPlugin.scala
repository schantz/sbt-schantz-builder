package com.schantz.sbt

import sbt._
import com.github.siasia.PluginKeys._
import com.github.siasia.WarPlugin._
import Keys._
import com.github.siasia.WarPlugin.packageWarTask
import _root_.sbt.Defaults.{ packageTasks, packageBinTask, inDependencies }
import Project.Initialize
import scala.xml._
import com.schantz.sbt.PluginKeys._

object MergeWebResourcesPlugin extends Plugin {
  def webSettings = {
    warResourceDirectoriesTask ++
      // default excludes
      inConfig(Compile)(Seq(warExcludedJars := Nil, warExcludedMetaInfResources := Nil)) ++
      // configure web app
      warSettings ++ Seq(
        warPostProcess in Compile <<= (target, streams, warExcludedJars, warExcludedMetaInfResources, warResourceDirectories, unmanagedClasspath in Compile) map {
          (target, streams, warExcludedJars, warExcludedMetaInfResources, warResourceDirectories, unmanagedClasspath) =>
            {
              () =>
                val warPath = target / "webapp"
                val warLibPath = warPath / "WEB-INF/lib"

                // remove excluded jar's
                streams.log.info("Removing user excluded jars from war: " + warExcludedJars)
                warExcludedJars.foreach(jar => IO.delete(warLibPath / jar))

                // remove duplicate jar's
                removeDuplicateJarsFromWar(unmanagedClasspath, warLibPath, streams)

                // remove unwanted meta-inf content
                streams.log.info("Excluding content from meta-inf: " + warExcludedMetaInfResources)
                warExcludedMetaInfResources.foreach(content => IO.delete(warPath / "META-INF" / content))

                // copy web resources from other projects
                warResourceDirectories.foreach(dir => {
                  safeCopy(dir, warPath / "WEB-INF/classes")
                })
            }
        },
        webappResources in Compile <+= (baseDirectory in Runtime)(sd => sd / "war"))
  }

  // retrieve the web resource directories for all dependent project
  private def warResourceDirectoriesTask = warResourceDirectories <<= {
    val key: SettingKey[Seq[File]] = resourceDirectories in Compile
    val resourceDirectoriesForDependencies = Defaults.inDependencies(key, _ => Nil)

    resourceDirectoriesForDependencies map { resourceSeq =>
      val dirs = resourceSeq.flatten
      // in dependencies traverse the projects in opposite order of the class-path so we must reverse result
      dirs.filter(dir => dir.exists()).reverse
    }
  }

  private def removeDuplicateJarsFromWar(fullClasspath: Classpath, warLibPath: File, out: TaskStreams) = {
    class JarVersionInfo(val name: String, val version: String, val jarFile: File);
    var extractVersionFromJar: (File => JarVersionInfo) = jar => {
      val jarRegex = """(.*?)(?:-(?=\d)(.*))?.jar""".r
      val jarRegex(name, version) = jar.getName
      new JarVersionInfo(name, version, jar)
    }

    var allJars = fullClasspath.flatMap { jar => if (jar.data.isFile()) Seq(extractVersionFromJar(jar.data)) else Seq() }
    var warJars = warLibPath.listFiles().filter(_.isFile()).map { jar => extractVersionFromJar(jar) }
    // all jar's contains the entire class path correctly ordered by dependent projects
    allJars.foreach { jarEntry =>
      var found = (warLibPath / jarEntry.jarFile.getName()).isFile()
      if (found) {
        // if a jar from the complete class path is in the war then search and delete other versions in the war file
        warJars.foreach { warEntry =>
          if (jarEntry.name == warEntry.name && jarEntry.version != warEntry.version) {
            out.log.warn("Excluding duplicate jar from war: " + warEntry.jarFile.getAbsolutePath())
            IO.delete(warEntry.jarFile)
          }
        }
      }
    }
  }

  // recursively copies files from source to destination, without overwriting any existing files
  private def safeCopy(sourceDir: File, destDir: File) = {
    val extractRelativePath = (sourceDir.getAbsolutePath() + "(.*)").r

    val filesToCopy = recursiveListFiles(sourceDir) flatMap { file =>
      val extractRelativePath(relativePath) = file.getAbsolutePath()
      val targetPath = destDir / relativePath
      if (targetPath.exists) {
        Nil
      } else {
        Seq((file, targetPath))
      }
    }

    IO.copy(filesToCopy)
  }

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }
}
