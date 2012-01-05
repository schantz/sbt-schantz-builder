sbtPlugin := true

name := "schantz-builder"

organization := "com.schantz"

version := "1.0-SNAPSHOT"

isSnapshot := true

sbtVersion := "0.11.2"

scalaVersion := "2.9.1"

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.10"))

libraryDependencies += "org.testng" % "testng" % "5.14" 

publishMavenStyle := false

//publishTo := Some( Resolver.file("file",  new File( "repo/public/schantz-builder" ))( 
//  Patterns(true,"[scalaVersion]/[artifact](-[classifier]).[ext]")) )
