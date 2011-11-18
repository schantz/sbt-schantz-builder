package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

trait SchantzWebBuild extends SchantzBuild {
  def warExcludedJars = PluginKeys.warExcludedJars

  def warExcludedMetaInfResources = PluginKeys.warExcludedMetaInfResources

  override def mySettings = {
    super.mySettings ++ Seq(
      artifactName := { (config: String, module: ModuleID, artifact: Artifact) =>
        artifact.name + "-" + module.revision + "." + artifact.extension
      }) ++ MergeWebResourcesPlugin.webSettings
  }
}