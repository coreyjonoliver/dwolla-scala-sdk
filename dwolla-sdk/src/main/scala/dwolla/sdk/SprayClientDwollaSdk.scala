package dwolla.sdk

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorSystem
import spray.client.pipelining._
import akka.util.Timeout
import spray.http._
import DwollaSdkJsonProtocol._
import spray.json._
import spray.can.client.HostConnectorSettings
import spray.can.Http.HostConnectorSetup
import spray.httpx.SprayJsonSupport._

class SprayClientDwollaSdk(settings: Option[HostConnectorSettings] = None)(
  implicit system: ActorSystem,
  timeout: Timeout,
  ec: ExecutionContext) extends SprayHttpClient with DwollaSdk {

  private val setup = HostConnectorSetup(
    host = "www.dwolla.com",
    port = 443,
    sslEncryption = true,
    settings = settings
  )

  private val requestPreparer: RequestTransformer = addHeader("Accept", "application/json")

  private def execute[T](req: HttpRequest): Future[HttpResponse] = pipeline(setup).flatMap(_(requestPreparer(req)))

  private def executeTo[T](req: HttpRequest, m: (HttpResponse => T)): Future[T] = execute(req).map(m)

  private def mapResponse[T: JsonFormat](response: HttpResponse) = {
    if (response.status.isSuccess) {
      val parsedResponse = response.entity.asString.asJson.convertTo[Response[T]]
      if (parsedResponse.success) {
        parsedResponse.response.get
      } else {
        throw new DwollaException(parsedResponse.message)
      }
    } else {
      throw new DwollaException("Unsuccessful response: " + response.entity.asString)
    }
  }

  def getAllTransactions(accessToken: String) = {
    val uri = Uri(s"/oauth/rest/transactions/").withQuery(Map("oauth_token" -> accessToken))
    executeTo[Seq[TransactionDetails]](Get(uri), mapResponse[Seq[TransactionDetails]])
  }

  def send(accessToken: String, pin: Int, destinationId: String, amount: BigDecimal): Future[Int] = {
    val uri = Uri("/oauth/rest/transactions/send")
    val raw = Map("oauth_token" -> accessToken, "pin" -> pin.toString,
      "destinationId" -> destinationId, "amount" -> amount.toString)
    executeTo[Int](Post(uri, raw), mapResponse[Int])
  }

  def getTransactionDetails(accessToken: String, transactionId: Int): Future[TransactionDetails] = {
    val uri = Uri(s"/oauth/rest/transactions/$transactionId").withQuery(Map("oauth_token" -> accessToken))
    executeTo[TransactionDetails](Get(uri), mapResponse[TransactionDetails])
  }

  def getBalance(accessToken: String): Future[BigDecimal] = {
    val uri = Uri("/oauth/rest/balance/").withQuery(Map("oauth_token" -> accessToken))
    executeTo[BigDecimal](Get(uri), mapResponse[BigDecimal])
  }

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformation] = {
    val uri = Uri("/oauth/rest/users/").withQuery(Map("oauth_token" -> accessToken))
    executeTo[FullAccountInformation](Get(uri),
      mapResponse[FullAccountInformation])
  }

  def getBasicAccountInformation(clientId: String, clientSecret: String,
                                 accountIdentifier: String): Future[BasicAccountInformation] = {
    val uri = Uri(s"/oauth/rest/users/$accountIdentifier")
    val uriWithQuery = uri.withQuery(Map("client_id" -> clientId, "client_secret" -> clientSecret))
    executeTo[BasicAccountInformation](Get(uriWithQuery), mapResponse[BasicAccountInformation])
  }

  def getNearby(clientId: String, clientSecret: String, latitude: BigDecimal,
                longitude: BigDecimal): Future[Seq[NearbyDetails]] = {
    val uri = Uri("/oauth/rest/users/nearby")
    val uriWithQuery = uri.withQuery(Map("client_id" -> clientId, "client_secret" -> clientSecret,
      "latitude" -> latitude.toString,
      "longitude" -> longitude.toString))
    executeTo[Seq[NearbyDetails]](Get(uriWithQuery), mapResponse[Seq[NearbyDetails]])
  }
}
