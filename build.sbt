scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings", "-target:jvm-1.7")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.6",
  "joda-time" % "joda-time" % "2.5",
  "org.joda" % "joda-convert" % "1.7",
  "io.spray" %% "spray-client" % "1.3.2",
  "com.typesafe.akka" %% "akka-stream-experimental" % "0.10",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test")

resolvers += "spray repo" at "http://repo.spray.io"
