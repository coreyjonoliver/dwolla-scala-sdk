package dwolla.sdk.examples

import scala.concurrent.{Await, ExecutionContext}
import dwolla.sdk.SprayClientDwollaSdk
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
  implicit val clientId = sys.env("DWOLLA_CLIENT_ID")
  implicit val clientSecret = sys.env("DWOLLA_SECRET")
  implicit val accessToken = sys.env("DWOLLA_ACCESS_TOKEN")

  val dwollaClient = new SprayClientDwollaSdk()

  val transactionDetailsFuture = dwollaClient.getTransactionDetails(accessToken, 3983417)
  val allTransactionsFuture = dwollaClient.getAllTransactions(accessToken)
  val balanceFuture = dwollaClient.getBalance(accessToken)
  val fullAccountInfoFuture = dwollaClient.getFullAccountInformation(accessToken)
  val basicAccountInfoFuture = dwollaClient.getBasicAccountInformation(clientId, clientSecret, "812-713-9234")
  val nearbyFuture = dwollaClient.getNearby(clientId, clientSecret, 40, -74)

  val transactionDetailsResult = Await.result(transactionDetailsFuture, timeout.duration)
  val allTransactionsResult = Await.result(allTransactionsFuture, timeout.duration)
  val balanceResult = Await.result(balanceFuture, timeout.duration)
  val fullAccountInfoResult = Await.result(fullAccountInfoFuture, timeout.duration)
  val basicAccountInfoResult = Await.result(basicAccountInfoFuture, timeout.duration)
  val nearbyResult = Await.result(nearbyFuture, timeout.duration)

  println(transactionDetailsResult)
  println(allTransactionsResult)
  println(balanceResult)
  println(fullAccountInfoResult)
  println(basicAccountInfoResult)
  println(nearbyResult)

  shutdown()

  def shutdown() {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
}