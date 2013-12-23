package examples;

object Main extends App {
   implicit val system = ActorSystem()
   implicit val timeout: Timeout = 1 minutes
   implicit val ec = ExecutionContext.global
   implicit val accessToken = ???

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
