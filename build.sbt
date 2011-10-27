sbtPlugin := true

name := "eclipsebuilder"

organization := "com.schantz"

version := "0.1-SNAPSHOT"

sbtVersion := "0.11.0"

scalaVersion := "2.9.1"

// we use functionality from the SBT web plugin
resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"    

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.8"))

libraryDependencies += "org.testng" % "testng" % "5.14"

