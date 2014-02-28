package dwolla.sdk

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorSystem
import spray.client.pipelining._
import akka.util.Timeout
import spray.http._
import spray.can.Http.HostConnectorSetup
import spray.http.HttpResponse
import spray.http.HttpHeaders.{`User-Agent`, Accept}
import dwolla.sdk.Requests._
import dwolla.sdk.Requests.AccountType._
import dwolla.sdk.Responses._
import spray.json._
import dwolla.sdk.DwollaApiAuthResponseJsonProtocol._
import dwolla.sdk.DwollaApiRequestJsonProtocol._
import dwolla.sdk.DwollaApiResponseJsonProtocol._
import spray.httpx.SprayJsonSupport._

private[sdk] class SprayClientDwollaApi(settings: Option[DwollaApiSettings] = None)(
  implicit system: ActorSystem,
  timeout: Timeout,
  ec: ExecutionContext) extends SprayHttpClient with DwollaApi {

  import spray.json.DefaultJsonProtocol._

  private val setup: HostConnectorSetup = {
    val setup = for {
      settings <- settings
      setup <- Some(HostConnectorSetup(host = settings.host, port = settings.port,
        sslEncryption = settings.sslEncryption))
    } yield setup
    setup.getOrElse(HostConnectorSetup(host = "www.dwolla.com", port = 443, sslEncryption = true))
  }


  private val requestPreparer: RequestTransformer = addHeaders(List(Accept(MediaTypes.`application/json`),
    `User-Agent`("Dwolla-Scala-SDK")))

  private def execute[T](req: HttpRequest): Future[HttpResponse] = pipeline(setup).flatMap(_(requestPreparer(req)))

  private def executeTo[T](req: HttpRequest, m: (HttpResponse => T)): Future[T] = execute(req).map(m)

  private def mapAuthResponse(response: HttpResponse) = {
    if (response.status.isSuccess) {
      try {
        response.entity.asString.asJson.convertTo[GetAccessTokenResponse]
      }
      catch {
        case e: DeserializationException =>
          val parsedResponse = response.entity.asString.asJson.convertTo[GetAccessTokenErrorResponse]
          throw new DwollaException(s"Error: ${parsedResponse.error}, Error description: ${
            parsedResponse
              .errorDescription
          }")
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

  def addFundingSource(accessToken: String, accountNumber: String, routingNumber: String, accountType: AccountType,
                       name: String): Future[AddFundingSourceResponse] = {
    val uri = Uri("/oauth/rest/fundingsources/")
    val raw = AddFundingSourceRequest2(accessToken, accountNumber, routingNumber, accountType, name)
    executeTo(Post(uri, raw), mapResponse[AddFundingSourceResponse])
  }

  def addFundingSource(accessToken: String, accountNumber: String, routingNumber: String, accountType: String,
                       name: String): Future[AddFundingSourceResponse] = {
    val uri = Uri("/oauth/rest/fundingsources/")
    val raw = AddFundingSourceRequest(accessToken, accountNumber, routingNumber, accountType, name)
    executeTo(Post(uri, raw), mapResponse[AddFundingSourceResponse])
  }

  def getFundingSourceDetails(accessToken: String, fundingId: String): Future[GetFundingSourceDetailsResponse] = {
    val uri = Uri(s"/oauth/rest/fundingsources/$fundingId")
    val uriWithQuery = uri.withQuery(Map("oauth_token" -> accessToken))
    executeTo(Get(uriWithQuery), mapResponse[GetFundingSourceDetailsResponse])
  }

  def depositFunds(accessToken: String, fundingId: String, pin: String,
                   amount: BigDecimal): Future[DepositFundsResponse] = {
    val uri = Uri(s"/oauth/rest/fundingsources/$fundingId/deposit")
    val raw = DepositFundsRequest(accessToken, fundingId, pin, amount)
    executeTo(Post(uri, raw), mapResponse[DepositFundsResponse])
  }

  def listFundingSources(accessToken: String): Future[ListFundingSourcesResponse] = {
    val uri = Uri("/oauth/rest/fundingsources/")
    val uriWithQuery = uri.withQuery(Map("oauth_token" -> accessToken))
    executeTo(Get(uriWithQuery), mapResponse[ListFundingSourcesResponse])
  }

  def withdrawFunds (accessToken: String, fundingId: String, pin: String,
    amount: BigDecimal): Future[WithdrawFundsResponse] =
  {
    val uri = Uri(s"/oauth/rest/fundingsources/$fundingId/withdraw")
    val raw = WithdrawFundsRequest(accessToken, fundingId, pin, amount)
    executeTo(Post(uri, raw), mapResponse[WithdrawFundsResponse])
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

  def listAllTransactions(accessToken: String, sinceDate: Option[String],
                          endDate: Option[String], types: Option[String], limit: Option[Int],
                          skip: Option[Int], groupId: Option[String]): Future[ListAllTransactionsResponse] = {
    val uri = Uri(s"/oauth/rest/transactions/")

    val optionalParams = Map("sinceDate" -> sinceDate,
        "endDate" -> endDate, "types" -> types, "limit" -> limit, "skip" -> skip,
        "groupId" -> groupId).filter(_._2.isDefined).map(x => (x._1, x._2.get.toString))

    val uriWithQuery = uri.withQuery(optionalParams + ("oauth_token" -> accessToken))
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
