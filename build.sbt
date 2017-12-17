name := "raytrace"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.11"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.11"

mainClass in (Compile, run) := Some("com.joe.raytrace.Main")