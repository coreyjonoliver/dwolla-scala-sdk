import sbt._
import Keys._

object BuildSettings {

  lazy val basicSettings = Seq(
    version := "0.1",
    homepage := Some(new URL("https://github.com/Dwolla/dwolla-scala")),
    organization := "com.dwolla",
    organizationHomepage := Some(new URL("https://www.dwolla.com")),
    description := "Scala interface to Dwolla's API",
    startYear := Some(2013),
    licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion := "2.10.3",
    resolvers ++= Dependencies.resolutionRepos,
    scalacOptions := Seq(
      "-encoding", "utf8",
      "-deprecation",
      "-target:jvm-1.6", // use the ASM Compiler backend to generate bytecode
      "-feature", // activate checking of SIP-18 features, forces import language
      "-unchecked", // enable detailed unchecked warnings
      "-Xlog-reflective-calls", // print a message when a reflective call is generated
      "-Ywarn-adapted-args" // warn if an argument list is modified to match the receiver
    )
  )

  lazy val dwollaModuleSettings = basicSettings
}