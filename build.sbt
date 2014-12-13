scalaVersion := "2.11.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings", "-target:jvm-1.7")

val sprayVersion = "1.3.2"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.7",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-M1",
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0-M1",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M1",
  "commons-codec" % "commons-codec" % "1.9",
  "joda-time" % "joda-time" % "2.5",
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.6",
  "org.joda" % "joda-convert" % "1.7",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

resourceDirectories in Test += (resourceDirectory in Compile).value
