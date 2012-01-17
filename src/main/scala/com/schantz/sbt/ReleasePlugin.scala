package com.schantz.sbt

import sbt.Process._
import sbt._
import Keys._
import com.schantz.sbt.PluginKeys._
import sbt.ProjectRef
import Project.Initialize
import sun.reflect.generics.reflectiveObjects.NotImplementedException

object ReleasePlugin extends Plugin {
  def releaseSettings = {
    Seq(releaseInfoTask)
  }

  // retrieve source control info for each dependent project
  private def releaseInfoTask = releaseInfo <<= {
    // TODO also get version info
    
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

          Seq((projectName, " Branch:" + branchName.trim, " Type:SVN", " URL:" + repoUrl.trim))
        } else {
          // TODO check git and hg here 
          val repoUrl = Process("hg" :: "showconfig" :: "paths.default" :: Nil, dir) !!
          val branchName = Process("hg" :: "branch" :: Nil, dir) !!

          Seq((projectName, " Branch:" + branchName.trim, " Type:HG", " URL:" + repoUrl.trim))
        } 
        /*else if((dir / ".git").exists()) {
          val repoUrl = Process("git" :: "remote" :: "show" :: "origin" :: Nil, dir) !!
          val branchName = Process("git" :: "branch" :: Nil, dir) !!

          Seq((projectName, " Branch:" + branchName.trim, " Type:GIT", " URL:" + repoUrl.trim))
        } else {
        	throw new UnsupportedOperationException("unknown source control system in " + dir.getAbsolutePath)
        }
        */
      }
      sourceDirs.foreach(println)
      sourceDirs
    }
  }
}