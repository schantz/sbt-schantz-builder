package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object DBBuildPlugin extends Plugin {
  def dbBuildSettings = {
    Seq(
        dbBuild in Global <<= (dbBuildClass in Compile, dbBuildName in Compile, dbBuildPath in Compile, fullClasspath in Compile) map dbBuildTask
    )
  }

  private def dbBuildTask(dbBuildClass:String, dbBuildName:String, dbBuildPath:String, fullClasspath:Seq[Attributed[File]]) = {
    assert(dbBuildClass.nonEmpty, "DB build class not specified correctly: " + dbBuildClass)
    assert(dbBuildName.nonEmpty, "DB build name not specified correctly: " + dbBuildName)
    assert(dbBuildPath.nonEmpty, "DB build path not specified correctly: " + dbBuildPath)

    runMain(dbBuildClass, List(dbBuildName, dbBuildPath), fullClasspath);
  }

  private def runMain(mainClass:String, args:Seq[String], classpath:Seq[Attributed[File]]) = {
    val cp = classpath.map { e => e.data.getAbsolutePath.replaceAll("\\\\E", "\\\\E\\\\\\\\E\\\\Q") }
    var cmd: String = "java "
    cmd += "-ea "
    cmd += "-Xmx1512m "
    cmd += "-Djavax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema=org.apache.xerces.jaxp.validation.XMLSchemaFactory "
    cmd += "-Xss1024k "
    cmd += "-XX:MaxPermSize=512m "
    cmd += "-cp " + cp.mkString(":")
    cmd += " " + mainClass
    cmd += " " + args.mkString(" ")
    print("LAUNCH " + cmd)

    cmd !;
  }
}