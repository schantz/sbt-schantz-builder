sbtPlugin := true

name := "eclipsebuilder"

organization := "com.schantz"

publishMavenStyle := true

version := "0.9"		

publishTo := Some(Resolver.file("Local", Path.userHome / "projects" /  "maven2" asFile)(Patterns(true, Resolver.mavenStyleBasePattern)))