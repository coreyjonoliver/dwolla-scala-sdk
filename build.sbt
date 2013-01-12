name := "dwolla_sdk_scala"

organization := "org.coreyoliver"

version := "0.1"

libraryDependencies ++=
Seq(
  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation")
