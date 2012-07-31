sbtPlugin := true

name := "sbt-schantz-builder"

organization := "com.schantz"

version := "1.0-SNAPSHOT"

isSnapshot := true

//sbtVersion := "0.11.2"

//scalaVersion := "2.9.1"

libraryDependencies <+= sbtVersion(v => v match {
case "0.11.0" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.0-0.2.8"
case "0.11.1" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.1-0.2.10"
case "0.11.2" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11"
case "0.11.3" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.3-0.2.11.1"
})

libraryDependencies += "org.testng" % "testng" % "5.14" 

publishMavenStyle := true

// disable publishing the main API jar
publishArtifact in (Compile, packageDoc) := false

// disable publishing the main sources jar
publishArtifact in (Compile, packageSrc) := false

publishTo := Some( Resolver.file("file",  new File( "repo/public/" ))(Resolver.mavenStylePatterns)) 
