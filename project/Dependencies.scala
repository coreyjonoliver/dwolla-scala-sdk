import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Spray" at "http://repo.spray.io/",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  def compile(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")

  def provided(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")

  def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")

  def runtime(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")

  def container(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  val sprayClient = "io.spray" % "spray-client" % "1.2.0"
  val sprayJson = "io.spray" % "spray-json_2.10" % "1.2.5"
  val akkaActor = "com.typesafe.akka" % "akka-actor_2.10" % "2.2.3"
  val nscalaTime = "com.github.nscala-time" %% "nscala-time" % "0.6.0"
  val specs2 = "org.specs2" %%  "specs2" % "2.2.3"
  val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.10.1"

}