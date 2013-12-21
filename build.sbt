name := "dwolla-scala"

version := "1.0"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Spray" at "http://repo.spray.io/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val spray = "1.0-M7"
  val akka = "2.0.5"
  Seq(
    "io.spray" % "spray-client" % spray,
    "io.spray" % "spray-json_2.9.2" % "1.2.3",
    "com.typesafe.akka" % "akka-actor" % akka,
    "org.clapper" %% "grizzled-slf4j" % "0.6.10",
    "org.specs2" %% "specs2" % "1.12.4" % "test"
  )
}