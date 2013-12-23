name := "dwolla-scala"

version := "1.0"

scalaVersion := "2.10.3"

resolvers ++= Seq(
  "Spray" at "http://repo.spray.io/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val spray = "1.2.0"
  val akka = "2.2.3"
  Seq(
    "io.spray" % "spray-client" % spray,
    "io.spray" % "spray-json_2.10" % "1.2.5",
    "com.typesafe.akka" % "akka-actor_2.10" % akka,
    "org.clapper" %% "grizzled-slf4j" % "1.0.1"
    )
}
