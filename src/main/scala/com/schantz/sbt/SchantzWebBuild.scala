package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._
import java.io.File

trait SchantzWebBuild extends SchantzBuild {
  override def mySettings = {
    super.mySettings ++ 
    MergeWebResourcesPlugin.webSettings ++ 
    EarPlugin.earSettings 
    //++ 
    //Seq(artifactPath in Compile in packageBin <<= artifactName in Compile in packageBin { 
    //	name => BuildHelper.getDeployDir / new File(name) 
   	//})
  }
}