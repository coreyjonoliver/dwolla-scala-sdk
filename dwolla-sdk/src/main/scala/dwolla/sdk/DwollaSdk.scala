package dwolla.sdk

import scala.concurrent.Future
import spray.can.client.HostConnectorSettings
import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import com.github.nscala_time.time.Imports._
import dwolla.sdk.DwollaApiResponseJsonProtocol.TransactionDetails

class DwollaSdk(settings: Option[HostConnectorSettings] = None)(
  implicit system: ActorSystem,
  timeout: Timeout,
  ec: ExecutionContext) {

  private val dwollaApi = new SprayClientDwollaApi(settings)

  case class FeeDetails(id: Int, amount: BigDecimal, `type`: String)

  case class Transaction(amount: BigDecimal, date: Option[DateTime], destinationId: String,
                         destinationName: String, id: Int, sourceId: String, sourceName: String,
                         `type`: String, userType: String, status: String, clearingDate: Option[DateTime],
                         notes: String, fees: Seq[FeeDetails])

  object Transaction {

    private def transactionDetails2Transaction(transactionDetails: TransactionDetails): Transaction = {
      val fees = transactionDetails.fees.getOrElse(List()).map(f => FeeDetails(f.id, f.amount, f.`type`))
      Transaction(transactionDetails.amount, transactionDetails.date, transactionDetails.destinationId,
        transactionDetails.destinationName, transactionDetails.id, transactionDetails.sourceId,
        transactionDetails.sourceName, transactionDetails.`type`, transactionDetails.userType,
        transactionDetails.status, transactionDetails.clearingDate, transactionDetails.notes, fees)
    }

    def create(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
               destinationType: Option[String] = None,
               facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
               notes: Option[String] = None,
               additionalFees: Option[Seq[FacilitatorFee]] = None, assumeAdditionalFees: Option[Boolean] = None):
    Future[Transaction] = {
      for {
        id <- dwollaApi.send(accessToken, pin, destinationId, amount, destinationType, facilitatorAmount,
          assumeCosts, notes, additionalFees, assumeAdditionalFees)
        transaction <- retrieve(accessToken, id)
      } yield transaction
    }

    def retrieve(accessToken: String, id: Int): Future[Transaction] = {
      for {
        transactionDetails <- dwollaApi.getTransactionDetails(accessToken, id)
      } yield transactionDetails2Transaction(transactionDetails)
    }

    def all(accessToken: String): Future[Seq[Transaction]] = {
      for {
        transactionDetails <- dwollaApi.getAllTransactions(accessToken)
      } yield transactionDetails.map(t => transactionDetails2Transaction(t))
    }
  }
}
