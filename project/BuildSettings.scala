import sbt._
import Keys._

object BuildSettings {

  lazy val basicSettings = Seq(
    version := "0.1",
    homepage := Some(new URL("https://github.com/Dwolla/dwolla-scala")),
    organization := "com.dwolla",
    organizationHomepage  := Some(new URL("https://www.dwolla.com")),
    description           := "Scala interface to Dwolla's API",
    startYear             := Some(2013),
    licenses              := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion          := "2.10.3",
    resolvers             ++= Dependencies.resolutionRepos
  )

  lazy val dwollaModuleSettings = basicSettings
}