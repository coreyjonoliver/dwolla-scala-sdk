name := "dwolla_sdk_scala"

organization := "org.coreyoliver"

version := "0.1"

scalaVersion := "2.9.2"

crossScalaVersions := Seq("2.9.1", "2.9.2")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-common" % "2.5-M1",
  "net.liftweb" %% "lift-util" % "2.5-M1",
  "net.liftweb" %% "lift-json" % "2.5-M1",
  "net.databinder.dispatch" %% "core" % "0.9.1",
  "net.databinder.dispatch" %% "lift-json" % "0.9.1" exclude("net.liftweb", "lift-json"),
  "joda-time" % "joda-time" % "2.1",
  "org.joda" % "joda-convert" % "1.1",
  "org.scalatest" %% "scalatest" % "1.8" % "test"
)

scalacOptions in (Compile, doc) ++= Opts.doc.title("Dwolla-Scala API Reference")

parallelExecution in Test := false
