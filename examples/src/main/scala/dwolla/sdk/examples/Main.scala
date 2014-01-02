package dwolla.sdk.examples

import scala.concurrent.{Await, ExecutionContext}
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

  val retrieveBalanceFuture = dwollaSdk.Balance.retrieve(accessToken)
  val createTransactionFuture = dwollaSdk.Transaction.create(accessToken, pin, "812-713-9234", .01)
  val retrieveTransactionFuture = dwollaSdk.Transaction.retrieve(accessToken, 3983417)
  val allTransactionFuture = dwollaSdk.Transaction.all(accessToken)
  val retrieveUserFuture1 = dwollaSdk.User.retrieve(clientId, clientSecret, "812-713-9234")
  val retrieveUserFuture2 = dwollaSdk.User.retrieve(accessToken)

  val retrieveBalanceResult = Await.result(retrieveBalanceFuture, timeout.duration)
  val createTransactionResult = Await.result(createTransactionFuture, timeout.duration)
  val retrieveTransactionResult = Await.result(retrieveTransactionFuture, timeout.duration)
  val allTransactionResult = Await.result(allTransactionFuture, timeout.duration)
  val retrieveUserResult1 = Await.result(retrieveUserFuture1, timeout.duration)
  val retrieveUserResult2 = Await.result(retrieveUserFuture2, timeout.duration)

  println(retrieveBalanceResult)
  println(createTransactionResult)
  println(retrieveTransactionResult)
  println(allTransactionResult)
  println(retrieveUserResult1)
  println(retrieveUserResult2)

  shutdown()

  def shutdown() {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
}