package com.schantz.sbt

import sbt._

class ExcludeClassesPlugin extends Plugin {
  /*
  mappings in (Compile, packageBin) ~= { (ms: Seq[(File, String)]) =>
    ms filter {
      case (file, toPath) =>
        toPath != "javax/servlet/Servlet.class"
    }
  }
  */
}