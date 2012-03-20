package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

trait SchantzWebBuild extends SchantzBuild {
  override def mySettings = {
    super.mySettings ++ MergeWebResourcesPlugin.webSettings ++ EarPlugin.earSettings
  }
}