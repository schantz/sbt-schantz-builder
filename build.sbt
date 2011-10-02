sbtPlugin := true

name := "eclipsebuilder"

organization := "com.schantz"

publishMavenStyle := true
		
publishTo := Some(Resolver.file("Local", Path.userHome / "projects" /  "maven2" asFile)(Patterns(true, Resolver.mavenStyleBasePattern)))