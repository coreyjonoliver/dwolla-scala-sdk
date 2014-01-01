package dwolla.sdk

import scala.concurrent.Future
import spray.can.client.HostConnectorSettings
import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import com.github.nscala_time.time.Imports._
import dwolla.sdk.DwollaApiResponseJsonProtocol.{TransactionListingResponse, TransactionByIdResponse}
import scala.language.implicitConversions

class DwollaSdk(settings: Option[HostConnectorSettings] = None)(
  implicit system: ActorSystem,
  timeout: Timeout,
  ec: ExecutionContext) {

  private val dwollaApi = new SprayClientDwollaApi(settings)

  object Mappings {
    implicit def transactionByIdResponse2Transaction(transactionByIdResponse: TransactionByIdResponse): Transaction = {
      val fees = transactionByIdResponse.fees.getOrElse(List()).map(f => Fee(f.id, f.amount, f.`type`))
      Transaction(transactionByIdResponse.amount, transactionByIdResponse.date, transactionByIdResponse.destinationId,
        transactionByIdResponse.destinationName, transactionByIdResponse.id, transactionByIdResponse.sourceId,
        transactionByIdResponse.sourceName, transactionByIdResponse.`type`, transactionByIdResponse.userType,
        transactionByIdResponse.status, transactionByIdResponse.clearingDate, transactionByIdResponse.notes, fees)
    }

    implicit def transactionListingResponse2TransactionSeq(transactionListingResponse: TransactionListingResponse):
    Seq[Transaction] = {
      transactionListingResponse.map(transactionByIdResponse2Transaction)
    }
  }

  case class Fee(id: Int, amount: BigDecimal, `type`: String)

  case class Transaction(amount: BigDecimal, date: Option[DateTime], destinationId: String,
                         destinationName: String, id: Int, sourceId: String, sourceName: String,
                         `type`: String, userType: String, status: String, clearingDate: Option[DateTime],
                         notes: String, fees: Seq[Fee])

  object Transaction {

    import Mappings._

    def create(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
               destinationType: Option[String] = None,
               facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
               notes: Option[String] = None,
               additionalFees: Option[Seq[FacilitatorFee]] = None, assumeAdditionalFees: Option[Boolean] = None):
    Future[Transaction] = {
      for {
        transactionSendResponse <- dwollaApi.transactionSend(accessToken, pin, destinationId, amount, destinationType, facilitatorAmount,
          assumeCosts, notes, additionalFees, assumeAdditionalFees)
        transactionFuture <- retrieve(accessToken, transactionSendResponse)
      } yield transactionFuture
    }

    def retrieve(accessToken: String, id: Int): Future[Transaction] = {
      for {
        transactionByIdResponse <- dwollaApi.getTransactionById(accessToken, id)
      } yield transactionByIdResponse
    }

    def all(accessToken: String): Future[Seq[Transaction]] = {
      for {
        transactionListingResponse <- dwollaApi.getTransactionListing(accessToken)
      } yield transactionListingResponse
    }
  }
}
