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
   implicit val timeout: Timeout = 1 minutes
   implicit val ec = ExecutionContext.global
   implicit val accessToken = sys.env("DWOLLA_ACCESS_TOKEN")

   val dwollaClient = new SprayClientDwollaSdk()

   val balanceFuture = dwollaClient.getBalance()
   val fullAccountInfoFuture = dwollaClient.getFullAccountInformation()

   val balanceResult = Await.result(balanceFuture, timeout.duration)
   val fullAccountInfoResult = Await.result(fullAccountInfoFuture, timeout.duration)

   println(balanceResult)
   println(fullAccountInfoResult)

   shutdown()

   def shutdown() {
     IO(Http).ask(Http.CloseAll)(1 second).await
     system.shutdown()
   }
 }