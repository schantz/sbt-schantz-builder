package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object DBBuildPlugin extends Plugin {
  def dbBuildSettings = {
    inConfig(Compile)(Seq(dbBuildClass := null, dbBuildName := null, dbBuildPath := null, dbBuildTask))
  }

  private def dbBuildTask = dbBuild <<= (dbBuildClass, dbBuildName, dbBuildPath, fullClasspath) map {
    (dbBuildClass, dbBuildName, dbBuildPath, fullClasspath) =>
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