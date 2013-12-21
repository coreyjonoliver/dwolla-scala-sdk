### Usage

_dwolla-scala_ is simple to use. Begin by providing the appropriate imports.  

```scala
import akka.actor._
import akka.dispatch.Await
import akka.util.duration._
import spray.can.client.HttpClient
import spray.client.HttpConduit
import spray.io._
import dwolla.api.SprayClientDwollaApi
```

additional setup may look like the following:

```scala
implicit val system = ActorSystem()
val ioBridge = IOExtension(system).ioBridge()
val httpClient = system.actorOf(Props(new HttpClient(ioBridge)))

val dwollaApiConduit = system.actorOf(
  props = Props(new HttpConduit(httpClient, "www.dwolla.com", 443, sslEnabled = true)),
  name = "dwolla-api-conduit"
)

val token = <oauth_token>

val dwollaApi = new SprayClientDwollaApi(dwollaApiConduit)
```

each Dwolla API endpoint is represented as a method on an instance of SprayClientDwollaApi. Each method returns a `Future`
of the response from the Dwolla API.

```scala
val balance = Await.result(dwollaApi.getBalance(token), 1 minutes)
```