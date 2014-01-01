package dwolla.sdk.examples

import scala.concurrent.{Await, ExecutionContext}
import dwolla.sdk.SprayClientDwollaApi
import dwolla.sdk.DwollaSdk
import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.io.IO
import spray.can.Http
import spray.util._

object Main extends App {
  implicit val system = ActorSystem()
  implicit val timeout: Timeout = 1.minutes
  implicit val ec = ExecutionContext.global

  val clientId = sys.env("DWOLLA_CLIENT_ID")
  val clientSecret = sys.env("DWOLLA_SECRET")
  val accessToken = sys.env("DWOLLA_ACCESS_TOKEN")
  val pin = sys.env("DWOLLA_PIN")

  val dwollaSdk = new DwollaSdk()

  val createTransactionFuture = dwollaSdk.Transaction.create(accessToken, pin, "812-713-9234", .01)

  val createTransactionResult = Await.result(createTransactionFuture, timeout.duration)

  println(createTransactionResult)

  shutdown()

  def shutdown() {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
}