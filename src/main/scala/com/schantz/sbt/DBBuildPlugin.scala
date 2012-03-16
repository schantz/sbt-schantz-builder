package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object DBBuildPlugin extends Plugin {
  def dbBuildSettings = {
    inConfig(Compile)(Seq(dbBuildTask, dbBuildClass := "", dbBuildName := "", dbBuildPath := ""))
  }

  private def dbBuildTask = dbBuild <<= (dbBuildClass, dbBuildName, dbBuildPath, fullClasspath) map {
    (dbBuildClass, dbBuildName, dbBuildPath, fullClasspath) =>
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