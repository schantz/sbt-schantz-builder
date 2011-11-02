package com.schantz.sbt

import com.schantz.sbt.PluginKeys._

trait SchantzWebBuild extends SchantzBuild {
  def excludeJarsFromWar = PluginKeys.excludeJarsFromWar
  
  override def mySettings = {
    super.mySettings ++ MergeWebResourcesPlugin.webSettings
  }
}