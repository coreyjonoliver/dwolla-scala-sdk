import sbt.Keys._
import sbt._

object Build extends Build {

  import BuildSettings._
  import Dependencies._

  lazy val root = Project(id = "root",
    base = file("."))
    .aggregate(dwollaSdk, examples)
    .settings(noPublishing: _*)

  lazy val dwollaSdk = Project(id = "dwolla-scala-sdk", base = file("dwolla-sdk"))
    .settings(dwollaModuleSettings: _*)
    .settings(libraryDependencies ++=
    provided(akkaActor, sprayClient, sprayJson, nscalaTime)
    )

  lazy val examples = Project(id = "examples", base = file("examples"))
    .dependsOn(dwollaSdk)
    .settings(dwollaModuleSettings: _*)
    .settings(noPublishing: _*)
    .settings(libraryDependencies ++=
    compile(sprayClient, sprayJson, akkaActor, nscalaTime)
    )
}