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
    val parsedResponse = response.entity.asString.asJson.convertTo[Response[T]]
    if (parsedResponse.success) {
      parsedResponse.response.get
    } else {
      throw new DwollaException(parsedResponse.message)
    }
  }

  def getBalance(accessToken: String): Future[BigDecimal] = {
    executeTo[BigDecimal](Get(s"/oauth/rest/balance/?oauth_token=$accessToken"), mapResponse[BigDecimal])
  }

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformation] = {
    executeTo[FullAccountInformation](Get(s"/oauth/rest/users/?oauth_token=$accessToken"),
      mapResponse[FullAccountInformation])
  }

  def getBasicAccountInformation(clientId: String, clientSecret: String,
                                 accountIdentifier: String): Future[BasicAccountInformation] = {
    executeTo[BasicAccountInformation](Get(s"/oauth/rest/users/$accountIdentifier?client_id=$clientId&client_secret" +
      s"=$clientSecret"), mapResponse[BasicAccountInformation])
  }

  def getNearby(clientId: String, clientSecret: String, latitude: BigDecimal,
                longitude: BigDecimal): Future[Seq[NearbyElement]] = {
    executeTo[Seq[NearbyElement]](Get(s"/oauth/rest/users/nearby?client_id=$clientId&client_secret=$clientSecret" +
      s"&latitude" +
      s"=$latitude&longitude=$longitude"), mapResponse[Seq[NearbyElement]])
  }
}
