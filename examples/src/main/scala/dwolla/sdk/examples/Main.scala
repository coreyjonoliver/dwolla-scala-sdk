package dwolla.sdk.examples

import scala.concurrent.{Await, ExecutionContext}
import dwolla.sdk.{AccountType, DwollaApiSettings, DwollaSdk}
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
  val apiHost = sys.env("DWOLLA_API_HOST")

  val apiSettings = new DwollaApiSettings(apiHost, 80, false)
  val dwollaSdk = new DwollaSdk(Some(apiSettings))


  //  val createAccessTokenFuture = dwollaSdk.AccessToken.create(clientId, clientSecret,
  // "ALWcF9lH7u4LVARsri8wjs48FKU=", None)
  // TODO add sdk call using new add funding sources endpoint
  val retrieveBalanceFuture = dwollaSdk.Balance.retrieve(accessToken)
  val depositTransactionFuture = dwollaSdk.Transaction.deposit(accessToken, "1e76ab3e46b99e0c71d59b0f34d30a3c", pin,
    1.00)
  val retrieveFundingSourcesFuture = dwollaSdk.FundingSource.retrieve(accessToken, "1e76ab3e46b99e0c71d59b0f34d30a3c")
//  val createFundingSourceFuture = dwollaSdk.FundingSource.create(accessToken, "123456789", "333333334", "Savings",
//    "My Savings")
  val createFundingSource2Future = dwollaSdk.FundingSource.create(accessToken, "123456789", "333333334",
    AccountType.Savings, "My Savings")
  val filteredFundingSourcesFuture = dwollaSdk.FundingSource.all(accessToken, Some("812-467-6276"), Some("Dwolla"))
  val listFundingSourcesFuture = dwollaSdk.FundingSource.all(accessToken)
  val withdrawTransactionFuture = dwollaSdk.Transaction.withdraw(accessToken, "1e76ab3e46b99e0c71d59b0f34d30a3c",
    pin, 0.01)
  val createTransactionFuture = dwollaSdk.Transaction.create(accessToken, pin, "812-713-9234", .01)
  val retrieveTransactionFuture = dwollaSdk.Transaction.retrieve(accessToken, 2386180)
  val filteredTransactionFuture = dwollaSdk.Transaction.all(accessToken, Some("2013-11-19T19:00:00Z"), None, None, Some(200), Some(500), None)
  val allTransactionFuture = dwollaSdk.Transaction.all(accessToken)
  val retrieveUserFuture1 = dwollaSdk.User.retrieve(clientId, clientSecret, "812-713-9234")
  val retrieveUserFuture2 = dwollaSdk.User.retrieve(accessToken)
  val nearbyUserFuture = dwollaSdk.User.nearby(clientId, clientSecret, 41.5908, 93.6208)

  val createAuthenticationUrlResult = dwollaSdk.AuthenticationUrl.create(clientId, List("Balance"))
  //  val createAccessToken = Await.result(createAccessTokenFuture, timeout.duration)
  val retrieveBalanceResult = Await.result(retrieveBalanceFuture, timeout.duration)
  val depositTransactionResult = Await.result(depositTransactionFuture, timeout.duration)
//  val createFundingSourceResult = Await.result(createFundingSourceFuture, timeout.duration)
  val createFundingSource2Result = Await.result(createFundingSource2Future, timeout.duration)
  val retrieveFundingSourcesResult = Await.result(retrieveFundingSourcesFuture, timeout.duration)
  val listFundingSourcesResult = Await.result(listFundingSourcesFuture, timeout.duration)
  val filteredFundingSourcesResult = Await.result(filteredFundingSourcesFuture, timeout.duration)
  val createTransactionResult = Await.result(createTransactionFuture, timeout.duration)
  val retrieveTransactionResult = Await.result(retrieveTransactionFuture, timeout.duration)
  val allTransactionResult = Await.result(allTransactionFuture, timeout.duration)
  val filteredTransactionResult = Await.result(filteredTransactionFuture, timeout.duration)
  val retrieveUserResult1 = Await.result(retrieveUserFuture1, timeout.duration)
  val retrieveUserResult2 = Await.result(retrieveUserFuture2, timeout.duration)
  val nearbyUserResult = Await.result(nearbyUserFuture, timeout.duration)

  println(createAuthenticationUrlResult)
  //  println(createAccessToken)
  println(retrieveBalanceResult)
  println(depositTransactionResult)
//  println(createFundingSourceResult)
  println(createFundingSource2Result)
  println(retrieveFundingSourcesResult)
  println(listFundingSourcesResult)
  println(filteredFundingSourcesResult)
  println(createTransactionResult)
  println(retrieveTransactionResult)
  println(allTransactionResult)
  println(filteredTransactionResult)
  println(retrieveUserResult1)
  println(retrieveUserResult2)
  println(nearbyUserResult)

  shutdown()

  def shutdown() {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
}