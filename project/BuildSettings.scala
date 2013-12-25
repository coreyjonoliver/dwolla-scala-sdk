import sbt._
import Keys._
import com.typesafe.sbt.SbtPgp

object BuildSettings {
  val VERSION = "1.0-SNAPSHOT"

  lazy val basicSettings = Seq(
    version := VERSION,
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

  lazy val dwollaModuleSettings =
    basicSettings ++
      Seq(
        // publishing
        crossPaths := false,
        publishMavenStyle := true,
        SbtPgp.useGpg := true,
        publishTo <<= version {
          version =>
            Some {
              val nexus = "https://oss.sonatype.org/"
              if (version endsWith "-SNAPSHOT") ("snapshots" at nexus + "content/repositories/snapshots")
              else ("releases" at nexus + "service/local/staging/deploy/maven2")
            }
        },
        pomIncludeRepository := {
          _ => false
        },
        pomExtra :=
          <url>https://developers.dwolla.com/dev</url>
            <licenses>
              <license>
                <name>Apache 2</name>
                <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
                <distribution>repo</distribution>
              </license>
            </licenses>
            <scm>
              <url>git://github.com/spray/spray.git</url>
              <connection>scm:git:git@github.com:coreyjonoliver/dwolla-sdk-scala.git</connection>
            </scm>
            <developers>
              <developer>
                <id>coreyjonoliver</id> <name>Corey Oliver</name>
              </developer>
            </developers>,
        credentials += Credentials("Sonatype Nexus Repository Manager",
          "oss.sonatype.org",
          "sys.env(\"DWOLLA_PUBLISH_USERNAME\")",
          "sys.env(\"DWOLLA_PUBLISH_PASSWORD\")")
      )
}