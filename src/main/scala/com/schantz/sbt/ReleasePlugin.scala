package com.schantz.sbt

import sbt.Process._
import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._
import sbt.ProjectRef
import Project.Initialize

object ReleasePlugin extends Plugin {
  def releaseSettings = {
    Seq(releaseTask)
  }

  // retrieve source control info for each dependent project
  private def releaseTask = release <<= {
    // ref points to current project, so define a function that given a ref executes a command on it 
    val getBaseDirectory: ProjectRef => Initialize[Option[File]] = ref => (baseDirectory in ref).?
    val baseDirectoryForDependencies = Defaults.forDependencies(getBaseDirectory)

    baseDirectoryForDependencies map { baseDirSeq =>
      val dirs = baseDirSeq.flatten
      val sourceDirs = dirs.flatMap { dir =>
        val projectName = dir.getAbsolutePath().substring(dir.getParentFile().getAbsolutePath().length() + 1)
        if ((dir / ".svn").exists()) {
          val svnOut = Process("svn" :: "info" :: Nil, dir) !!

          val urlMatcher = """(?sm).*URL: (\S*).*Repository Root: (\S*).*""".r
          val urlMatcher(repoUrl, repoRoot) = svnOut
          // TODO extract branch name correctly
          var branchName = "trunk"

          Seq((projectName, " Branch:" + branchName.trim, " Type:SVN", repoUrl.trim))
        } else {
          val repoUrl = Process("hg" :: "showconfig" :: "paths.default" :: Nil, dir) !!
          val branchName = Process("hg" :: "branch" :: Nil, dir) !!

          Seq((projectName, " Branch:" + branchName.trim, " Type:HG", repoUrl.trim))
        }
      }
      sourceDirs.foreach(println)
      sourceDirs
    }
  }
}