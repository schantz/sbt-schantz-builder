package com.schantz.sbt

import com.schantz.sbt.PluginKeys._

trait SchantzWebBuild extends SchantzBuild {
  def warExcludedJars = PluginKeys.warExcludedJars

  def warExcludedMetaInfResources = PluginKeys.warExcludedMetaInfResources

  override def mySettings = {
    super.mySettings ++ MergeWebResourcesPlugin.webSettings
  }
}