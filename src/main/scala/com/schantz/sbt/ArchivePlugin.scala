package com.schantz.sbt

import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._

object ArchivePlugin extends Plugin {
  def archiveSettings = {
    Seq(
        archive in Global <<= (archivePath in Compile, streams) map archiveTask
    )
  }

  private def archiveTask(archivePath:String, out: TaskStreams) = {
    assert(archivePath.nonEmpty, "archive path not specified correctly: " + archivePath)
    var archiveTo = new File(archivePath);
    assert(archiveTo.exists);

    var files = (BuildHelper.getDeployDir ** "*.*").get x (relativeTo(archiveTo) | flat) 

    out.log.info("archiving: " + files)
    // TODO IO.move(files)
  }
}