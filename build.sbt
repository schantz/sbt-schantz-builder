sbtPlugin := true

name := "schantz-builder"

organization := "com.schantz"

version := "0.1-SNAPSHOT"

sbtVersion := "0.11.1"

scalaVersion := "2.9.1"

// we use functionality from the SBT web plugin
resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"    

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.8"))

libraryDependencies += "org.testng" % "testng" % "5.14"

publishTo := Some( Resolver.file("file",  new File( "repo/public/schantz-builder" ))( Patterns("[scalaVersion]/[artifact].[ext]")) )
