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

    val cp = fullClasspath.map { e => e.data.getAbsolutePath }
    // TODO make into a general function for executing java processes
    var cmd: String = "java "
    cmd += "-ea "
    cmd += "-Xmx1200m "
    cmd += "-Djavax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema=org.apache.xerces.jaxp.validation.XMLSchemaFactory "
    cmd += "-Xss1024k "
    cmd += "-XX:MaxPermSize=256m "
    cmd += "-cp " + cp.mkString(":")
    cmd += " " + dbBuildClass
    // args
    cmd += " " + dbBuildName
    cmd += " " + dbBuildPath

    cmd !;
  }
}