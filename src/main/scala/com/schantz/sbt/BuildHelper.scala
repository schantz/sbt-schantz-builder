package com.schantz.sbt

import sbt._
import java.io.File

object BuildHelper extends Plugin {
	def getDeployDir: File = {
		val repo = System.getProperty("JarRepository")
		val deployDir = new File(repo) / "deploy"
		return deployDir
	}

	/** 
	 * Scan base directory for version info
	 */
	def findVersionNumber(basedir: File, projectName:String): String = {
		import scala.io._
		if(getRootProperty("version", projectName) != null) {
			return getRootProperty("version", projectName);
		}
		val versionFiles = Seq((basedir / "resources/build.version"), (basedir / "src/main/resources/build.version")).filter(_.exists())

		var majorVersion = "1."
		var minorVersion = "0"

		versionFiles.foreach { file =>
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
		versionNumber
	}

	def getRootProperty(propertyName:String, projectName:String): String = {
		if(System.getProperty(propertyName) != null && System.getProperty("root.project") == projectName) {
			return System.getProperty(propertyName);
		}
		return null;
	}
}
