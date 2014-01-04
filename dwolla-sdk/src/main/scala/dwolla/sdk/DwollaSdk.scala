package dwolla.sdk

import scala.concurrent.Future
import spray.can.client.HostConnectorSettings
import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import com.github.nscala_time.time.Imports._
import dwolla.sdk.DwollaApiResponseJsonProtocol._
import scala.language.implicitConversions
import scala.Some
import dwolla.sdk.DwollaApiResponseJsonProtocol.FullAccountInformationResponse
import dwolla.sdk.DwollaApiResponseJsonProtocol.GetTransactionDetailsResponse
import dwolla.sdk.DwollaApiResponseJsonProtocol.BasicAccountInformationResponse

class DwollaSdk(settings: Option[HostConnectorSettings] = None)(
  implicit system: ActorSystem,
  timeout: Timeout,
  ec: ExecutionContext) {

  private val dwollaApi = new SprayClientDwollaApi(settings)

  object Mappings {
    implicit def getTransactionDetailsResponse2Transaction(response:
                                                           GetTransactionDetailsResponse): Transaction = {
      val fees = response.fees.getOrElse(List()).map(f => Fee(f.id, f.amount, f.`type`))
      Transaction(response.amount, response.date,
        response.destinationId,
        response.destinationName, response.id,
        Some(response.sourceId),
        Some(response.sourceName), response.`type`,
        response.userType,
        response.status, response.clearingDate,
        response.notes, fees)
    }

    implicit def addFundingSourceResponse2FundingSource(response: AddFundingSourceResponse): FundingSource = {
      FundingSource(response.id, response.name, response.`type`, response.verified, response.processingType)
    }

    implicit def getFundingSourceDetailsResponse2FundingSource(response: GetFundingSourceDetailsResponse):
    FundingSource = {
      FundingSource(response.id, response.name, response.`type`, response.verified, response.processingType)
    }

    implicit def listAllTransactionsResponse2TransactionSeq(response: ListAllTransactionsResponse):
    Seq[Transaction] = {
      val listAllTransactionsResponseElement2Transaction = getTransactionDetailsResponse2Transaction _
      response.map(listAllTransactionsResponseElement2Transaction)
    }

    implicit def depositFundsResponse2Transaction(response: DepositFundsResponse): Transaction = {
      Transaction(response.amount, response.date,
        response.destinationId,
        response.destinationName, response.id,
        Some(response.sourceId),
        Some(response.sourceName), response.`type`,
        response.userType,
        response.status, response.clearingDate,
        response.notes, List())
    }

    implicit def basicAccountInformationResponse2User(response: BasicAccountInformationResponse): User = {
      User(response.id, response.latitude, response.longitude, response.name, None, None, None, None)
    }

    implicit def fullAccountInformationResponse2User(response: FullAccountInformationResponse): User = {
      User(response.id, response.latitude, response.longitude, response.name, Some(response.city),
        Some(response.state), Some(response.`type`), None)
    }

    implicit def findUsersNearbyResponse2UserSeq(response: FindUsersNearbyResponse): Seq[User] = {
      response.map(x => User(x.id, x.latitude, x.longitude, x.name, None, None, None, Some(x.image)))
    }
  }

  type AccessToken = String

  object AccessToken {
    def create(clientId: String, clientSecret: String, code: String, redirectUri: Option[String] = None) = {
      for {
        tokenResponse <- dwollaApi.getAccessToken(clientId, clientSecret, code, redirectUri)
      } yield tokenResponse
    }
  }

  type Balance = BigDecimal

  object Balance {
    def retrieve(accessToken: String): Future[Balance] = {
      for {
        balanceResponse <- dwollaApi.getBalance(accessToken)
      } yield balanceResponse
    }
  }

  case class FundingSource(id: String, name: String, `type`: String, verified: Boolean,
                           processingType: String)

  object FundingSource {

    import Mappings._

    def create(accessToken: String, accountNumber: String, routingNumber: String, accountType: String,
               name: String): Future[FundingSource] = {
      for {
        addFundingSourceResponse <- dwollaApi.addFundingSource(accessToken, accountNumber, routingNumber,
          accountType, name)
      } yield addFundingSourceResponse
    }

    def retrieve(accessToken: String, id: Int): Future[FundingSource] = {
      for {
        getFundingSourceDetailsResponse <- dwollaApi.getFundingSourceDetails(accessToken, id)
      } yield getFundingSourceDetailsResponse
    }
  }

  case class Fee(id: Int, amount: BigDecimal, `type`: String)

  case class Transaction(amount: BigDecimal, date: Option[DateTime], destinationId: String,
                         destinationName: String, id: Int, sourceId: Option[String], sourceName: Option[String],
                         `type`: String, userType: String, status: String, clearingDate: Option[DateTime],
                         notes: String, fees: Seq[Fee])

  object Transaction {

    import Mappings._

    def create(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
               destinationType: Option[String] = None,
               facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
               notes: Option[String] = None,
               additionalFees: Seq[FacilitatorFee] = List(), assumeAdditionalFees: Option[Boolean] = None):
    Future[Transaction] = {
      for {
        transactionSendResponse <- dwollaApi.sendMoney(accessToken, pin, destinationId, amount, destinationType,
          facilitatorAmount,
          assumeCosts, notes, additionalFees, assumeAdditionalFees)
        transactionFuture <- retrieve(accessToken, transactionSendResponse)
      } yield transactionFuture
    }

    def retrieve(accessToken: String, id: Int): Future[Transaction] = {
      for {
        transactionByIdResponse <- dwollaApi.getTransactionDetails(accessToken, id)
      } yield transactionByIdResponse
    }

    def all(accessToken: String): Future[Seq[Transaction]] = {
      for {
        transactionListingResponse <- dwollaApi.listAllTransactions(accessToken)
      } yield transactionListingResponse
    }

    def deposit(accessToken: String, fundingId: Int, pin: String, amount: BigDecimal): Future[Transaction] = {
      for {
        depositFundsResponse <- dwollaApi.depositFunds(accessToken, fundingId, pin, amount)
      } yield depositFundsResponse
    }
  }

  case class User(id: String, latitude: BigDecimal, longitude: BigDecimal, name: String, city: Option[String],
                  state: Option[String], `type`: Option[String], image: Option[String])

  object User {

    import Mappings._

    def retrieve(clientId: String, clientSecret: String, accountIdentifier: String): Future[User] = {
      for {
        basicAccountInformationResponse <- dwollaApi.getBasicAccountInformation(clientId, clientSecret,
          accountIdentifier)
      } yield basicAccountInformationResponse
    }

    def retrieve(accessToken: String): Future[User] = {
      for {
        fullAccountInformationResponse <- dwollaApi.getFullAccountInformation(accessToken)
      } yield fullAccountInformationResponse
    }

    def nearby(clientId: String, clientSecret: String, latitude: BigDecimal,
               longitude: BigDecimal): Future[Seq[User]] = {
      for {
        findUsersNearbyResponse <- dwollaApi.findUsersNearby(clientId, clientSecret, latitude, longitude)
      } yield findUsersNearbyResponse
    }
  }

}
