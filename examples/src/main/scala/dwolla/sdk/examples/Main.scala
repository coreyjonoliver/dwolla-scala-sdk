package dwolla.sdk.examples;

import scala.util.{Success, Failure}
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

import akka.actor.ActorSystem
//import akka.pattern.ask
//import akka.event.Logging
//import akka.io.IO
//import spray.json.{JsonFormat, DefaultJsonProtocol}
//import spray.can.Http
//import spray.httpx.SprayJsonSupport
//import spray.client.pipelining._
import spray.util._

object Main extends App {
   implicit val system = ActorSystem()
   implicit val timeout: Timeout = 1 minutes
   implicit val ec = ExecutionContext.global
   implicit val accessToken = "some-token"

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
