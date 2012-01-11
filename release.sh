#!/usr/bin/env scala
!#

import scala.sys.process.Process

if (args.isEmpty) {
  println("Usage: release 'fix/feature message'")
  System.exit(1)
}

var message = args.mkString(" ")

var codeDir = new java.io.File("")
var repoDir = new java.io.File("repo")

List(codeDir, repoDir).foreach { dir =>
  Process("git commit -am " + message + dir.getAbsolutePath)!   
  //Process("git push origin master " + dir.getAbsolutePath)!  
}
