package com.schantz.sbt

import sbt._

class ExcludePlugin extends Plugin {
  //val warPath = target / "webapp"
  //val warLibPath = warPath / "WEB-INF/lib"
  // remove unwanted jar's
  //val jarsToRemove = List("servlet-api.jar", "gwt-user.jar", "gwt-dev.jar")
  //jarsToRemove.foreach(jar => IO.delete(warLibPath / jar))

  // TODO grab list of jar's from 
  /*
  unmanagedClasspath in Compile <++= baseDirectory map { base =>
    val lib = base / "lib"
    Seq(
      lib / "testng.jar",
      lib / "gwt/2.3/gwt-user.jar",
      lib / "jetty/servlet.jar")
  }
  */
}