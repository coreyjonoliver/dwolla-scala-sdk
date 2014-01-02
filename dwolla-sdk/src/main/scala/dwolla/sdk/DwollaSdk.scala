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

    implicit def listAllTransactionsResponse2TransactionSeq(response: ListAllTransactionsResponse):
    Seq[Transaction] = {
      response.map(getTransactionDetailsResponse2Transaction)
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
  }

  type Balance = BigDecimal

  object Balance {
    def retrieve(accessToken: String): Future[Balance] = {
      for {
        balanceResponse <- dwollaApi.getBalance(accessToken)
      } yield balanceResponse
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
