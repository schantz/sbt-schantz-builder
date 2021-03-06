package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object DBBuildPlugin extends Plugin {
  def dbBuildSettings = {
    Seq(
        dbBuild in Global <<= (dbBuildClass in Compile, dbBuildPath in Compile, fullClasspath in Compile) map dbBuildTask
    )
  }

  private def dbBuildTask(dbBuildClass:String, dbBuildPath:String, fullClasspath:Seq[Attributed[File]]) = {
    var dbBuildName = System.getProperty("dbBuildName")
    assert(dbBuildClass != null && dbBuildClass.nonEmpty, "DB build class not specified correctly: " + dbBuildClass)
    assert(dbBuildName != null && dbBuildName.nonEmpty, "DB build name not specified correctly: " + dbBuildName)
    assert(dbBuildPath != null && dbBuildPath.nonEmpty, "DB build path not specified correctly: " + dbBuildPath)

    val f = file(dbBuildPath);
    if (f.isDirectory()) {
      f.listFiles().filter(file => file.getName().endsWith(".bak")).foreach(file => runMain(dbBuildClass, List(dbBuildName, file.getAbsolutePath()), fullClasspath))
    } else {
    	runMain(dbBuildClass, List(dbBuildName, dbBuildPath), fullClasspath);
    }
  }

  private def runMain(mainClass:String, args:Seq[String], classpath:Seq[Attributed[File]]) = {
    val cp = classpath.map { e => e.data.getAbsolutePath }
    val isWindows = System.getProperty("os.name").toLowerCase().contains("win")
    var cmd: String = "java "
    cmd += "-ea "
    cmd += "-Dfile.encoding=UTF8 "
    cmd += "-XX:MaxPermSize=512m "
    cmd += "-Xmx1512m "
    cmd += "-Djavax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema=org.apache.xerces.jaxp.validation.XMLSchemaFactory "
    cmd += "-Xss1024k "
    cmd += "-classpath " + cp.mkString(if(isWindows) ";" else ":")
    cmd += " " + mainClass
    cmd += " " + args.mkString(" ")
    print("RUN-MAIN\n" + cmd)

    val exitCode = cmd !;
    exitCode match {
      case 0 => {
          println("Java successfully executed")
      }
      case 1 => {
        throw new RuntimeException(mainClass+ " didn't execute successfully")
      }
      case _ => {
        throw new RuntimeException(mainClass+ " didn't execute successfully")
      }
    }
  }
}