package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object DBBuildPlugin extends Plugin {
  def dbBuildSettings = {
    inConfig(Compile)(Seq(
      dbBuildTask,
      dbBuildClass := "com.schantz.foundation.util.db.mssql.BpDbBuilderTest"))
  }

  private def dbBuildTask = dbBuild <<= (dbBuildClass, streams, fullClasspath) map {
    (dbBuildClass, streams, fullClasspath) =>
    val cp = fullClasspath.map { e => e.data.getAbsolutePath }
    //Some(dbBuildClass),
    var cmd: String = "java "
    cmd += "-ea "
    cmd += "-Xmx1200m "
    cmd += "-Djavax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema=org.apache.xerces.jaxp.validation.XMLSchemaFactory "
    cmd += "-Xss1024k "
    cmd += "-XX:MaxPermSize=256m "
    cmd += "-cp " + cp.mkString(":")
    cmd += " " + dbBuildClass + " "
    // args
    cmd += "lt-sbt-build "
    cmd += "//nas.schantz.com/GalopDbUpload/dbDumps/unpatched/Bp/BankPensionAdvLife_full.bak"

    cmd !;
    //val output: String = cmd !!;
    //streams.log.info("DBBUILD OUTPUT \n" + output)
  }
}