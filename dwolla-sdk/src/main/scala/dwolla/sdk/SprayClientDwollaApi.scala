package dwolla.sdk

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorSystem
import spray.client.pipelining._
import akka.util.Timeout
import spray.http._
import DwollaApiResponseJsonProtocol._
import spray.json._
import spray.can.client.HostConnectorSettings
import spray.can.Http.HostConnectorSetup
import spray.httpx.SprayJsonSupport._
import dwolla.sdk.DwollaApiRequestJsonProtocol.{AddFundingSourceRequest, SendAsGuestRequest, RefundRequest,
SendRequest, DepositFundsRequest}
import spray.http.HttpRequest
import dwolla.sdk.DwollaApiRequestJsonProtocol.DepositFundsRequest
import dwolla.sdk.DwollaApiResponseJsonProtocol.Response
import dwolla.sdk.DwollaApiRequestJsonProtocol.AddFundingSourceRequest
import dwolla.sdk.DwollaApiRequestJsonProtocol.SendRequest
import scala.Some
import dwolla.sdk.DwollaApiResponseJsonProtocol.IssueRefundResponse
import dwolla.sdk.DwollaApiRequestJsonProtocol.SendAsGuestRequest
import dwolla.sdk.DwollaApiRequestJsonProtocol.RefundRequest
import dwolla.sdk.DwollaApiResponseJsonProtocol.GetFundingSourceDetailsResponse
import dwolla.sdk.DwollaApiResponseJsonProtocol.FullAccountInformationResponse
import spray.http.HttpResponse
import dwolla.sdk.DwollaApiResponseJsonProtocol.AddFundingSourceResponse
import dwolla.sdk.DwollaApiResponseJsonProtocol.GetTransactionDetailsResponse
import dwolla.sdk.DwollaApiResponseJsonProtocol.GetAccessTokenResponse
import dwolla.sdk.DwollaApiResponseJsonProtocol.BasicAccountInformationResponse
import dwolla.sdk.DwollaApiResponseJsonProtocol.DepositFundsResponse


class SprayClientDwollaApi(settings: Option[DwollaApiSettings] = None)(
  implicit system: ActorSystem,
  timeout: Timeout,
  ec: ExecutionContext) extends SprayHttpClient with DwollaApi {

  private val setup: HostConnectorSetup = {
    val setup = for {
      settings <- settings
      setup <- Some(HostConnectorSetup(host = settings.host, port = settings.port,
        sslEncryption = settings.sslEncryption))
    } yield setup
    setup.getOrElse(HostConnectorSetup(host = "www.dwolla.com", port = 443, sslEncryption = true))
  }

  private val requestPreparer: RequestTransformer = addHeader("Accept", "application/json")

  private def execute[T](req: HttpRequest): Future[HttpResponse] = pipeline(setup).flatMap(_(requestPreparer(req)))

  private def executeTo[T](req: HttpRequest, m: (HttpResponse => T)): Future[T] = execute(req).map(m)

  private def mapAuthResponse(response: HttpResponse) = {
    if (response.status.isSuccess) {
      try {
        response.entity.asString.asJson.convertTo[GetAccessTokenResponse]
      }
      catch {
        case e: DeserializationException => {
          val parsedResponse = response.entity.asString.asJson.convertTo[GetAccessTokenErrorResponse]
          throw new DwollaException(s"Error: ${parsedResponse.error}, Error description: ${
            parsedResponse
              .errorDescription
          }")
        }
      }
    }
    else throw new DwollaException("Unsuccessful response: " + response.entity.asString)
  }

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

  def getAccessToken(clientId: String, clientSecret: String, code: String, redirectUri: Option[String] = None):
  Future[GetAccessTokenResponse] = {
    val uri = Uri("/oauth/v2/token")
    val uriWithQuery = uri.withQuery(Map("client_id" -> Some(clientId),
      "client_secret" -> Some(clientSecret),
      "grant_type" -> Some("authorization_code"), "redirect_uri" -> redirectUri, "code" -> Some(code)).flatMap {
      case (_, None) => None
      case (k, Some(v)) => Some(k -> v)
    })
    executeTo(Get(uriWithQuery), mapAuthResponse)
  }

  def getBalance(accessToken: String): Future[GetBalanceResponse] = {
    val uri = Uri("/oauth/rest/balance/")
    val uriWithQuery = uri.withQuery(Map("oauth_token" -> accessToken))
    executeTo(Get(uriWithQuery), mapResponse[GetBalanceResponse])
  }

  def addFundingSource(accessToken: String, accountNumber: String, routingNumber: String, accountType: String,
                       name: String): Future[AddFundingSourceResponse] = {
    val uri = Uri("/oauth/rest/fundingsources")
    val raw = AddFundingSourceRequest(accessToken, accountNumber, routingNumber, accountType, name)
    executeTo(Post(uri, raw), mapResponse[AddFundingSourceResponse])
  }

  def getFundingSourceDetails(accessToken: String, fundingId: Int): Future[GetFundingSourceDetailsResponse] = {
    val uri = Uri(s"/fundingsources/$fundingId")
    val uriWithQuery = uri.withQuery(Map("oauth_token" -> accessToken))
    executeTo(Get(uriWithQuery), mapResponse[GetFundingSourceDetailsResponse])
  }

  def depositFunds(accessToken: String, fundingId: Int, pin: String,
                   amount: BigDecimal): Future[DepositFundsResponse] = {
    val uri = Uri(s"/oauth/rest/fundingsources/$fundingId/deposit")
    val raw = DepositFundsRequest(accessToken, fundingId, pin, amount)
    executeTo(Post(uri, raw), mapResponse[DepositFundsResponse])
  }

  def getTransactionDetails(accessToken: String, transactionId: Int) = {
    val uri = Uri(s"/oauth/rest/transactions/$transactionId")
    val uriWithQuery = uri.withQuery(Map("oauth_token" -> accessToken))
    executeTo(Get(uriWithQuery), mapResponse[GetTransactionDetailsResponse])
  }

  def sendMoneyAsGuest(clientId: String, clientSecret: String, destinationId: String, amount: BigDecimal,
                       firstName: String, lastName: String, emailAddress: String, routingNumber: String,
                       accountNumber: String, accountType: String, assumeCosts: Option[Boolean] = None,
                       destinationType: Option[String] = None, notes: Option[String] = None, groupId: Option[Int],
                       additionalFees: Seq[FacilitatorFee] = List(), facilitatorAmount: Option[BigDecimal] = None,
                       assumeAdditionalFees: Option[Boolean] = None): Future[SendMoneyAsGuestResponse] = {
    val uri = Uri("/oauth/rest/transactions/guestsend")
    val raw = SendAsGuestRequest(clientId, clientSecret, destinationId, amount, firstName, lastName, emailAddress,
      routingNumber, accountNumber, accountType, assumeCosts, destinationType, notes, groupId, additionalFees,
      facilitatorAmount, assumeAdditionalFees)
    executeTo(Post(uri, raw), mapResponse[SendMoneyAsGuestResponse])
  }

  def listAllTransactions(accessToken: String): Future[ListAllTransactionsResponse] = {
    val uri = Uri(s"/oauth/rest/transactions/")
    val uriWithQuery = uri.withQuery(Map("oauth_token" -> accessToken))
    executeTo(Get(uriWithQuery), mapResponse[ListAllTransactionsResponse])
  }

  def issueRefund(accessToken: String, pin: String, transactionId: Int, fundsSource: Int, amount: BigDecimal,
                  notes: Option[String] = None) = {
    val uri = Uri("/oauth/rest/transactions/refund")
    val raw = RefundRequest(accessToken, pin, transactionId, fundsSource, amount, notes)
    executeTo(Post(uri, raw), mapResponse[IssueRefundResponse])
  }

  def sendMoney(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
                destinationType: Option[String] = None,
                facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
                notes: Option[String] = None,
                additionalFees: Seq[FacilitatorFee] = List(), assumeAdditionalFees: Option[Boolean] = None):
  Future[SendMoneyResponse] = {
    val uri = Uri("/oauth/rest/transactions/send")
    val raw = SendRequest(accessToken, pin, destinationId, amount, destinationType, facilitatorAmount, assumeCosts,
      notes, additionalFees, assumeAdditionalFees)
    executeTo(Post(uri, raw), mapResponse[SendMoneyResponse])
  }

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformationResponse] = {
    val uri = Uri("/oauth/rest/users/")
    val uriWithQuery = uri.withQuery(Map("oauth_token" -> accessToken))
    executeTo(Get(uriWithQuery), mapResponse[FullAccountInformationResponse])
  }

  def getBasicAccountInformation(clientId: String, clientSecret: String,
                                 accountIdentifier: String): Future[BasicAccountInformationResponse] = {
    val uri = Uri(s"/oauth/rest/users/$accountIdentifier")
    val uriWithQuery = uri.withQuery(Map("client_id" -> clientId, "client_secret" -> clientSecret))
    executeTo(Get(uriWithQuery), mapResponse[BasicAccountInformationResponse])
  }

  def findUsersNearby(clientId: String, clientSecret: String, latitude: BigDecimal,
                      longitude: BigDecimal): Future[FindUsersNearbyResponse] = {
    val uri = Uri("/oauth/rest/users/nearby")
    val uriWithQuery = uri.withQuery(Map("client_id" -> clientId, "client_secret" -> clientSecret,
      "latitude" -> latitude.toString,
      "longitude" -> longitude.toString))
    executeTo(Get(uriWithQuery), mapResponse[FindUsersNearbyResponse])
  }
}
