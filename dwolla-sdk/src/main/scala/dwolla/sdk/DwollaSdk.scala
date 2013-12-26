package dwolla.sdk

import scala.concurrent.Future
import dwolla.sdk.DwollaSdkResponseJsonProtocol._

private[sdk] case class FacilitatorFee(destinationId: String,
                                       amount: BigDecimal)

private[sdk] trait DwollaSdk {

  def getTransactionDetails(accessToken: String, transactionId: Int): Future[TransactionDetails]

  def send(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
           destinationType: Option[String] = None,
           facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
           notes: Option[String] = None,
           additionalFees: Option[Seq[FacilitatorFee]] = None, assumeAdditionalFees: Option[Boolean] = None):
  Future[Int]

  def getAllTransactions(accessToken: String): Future[Seq[TransactionDetails]]


  def refund(accessToken: String, pin: String, transactionId: Int, fundsSource: Int, amount: BigDecimal,
             notes: Option[String]): Future[RefundResponse]

  def getBalance(accessToken: String): Future[BigDecimal]

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformation]

  def getBasicAccountInformation(clientId: String, clientSecret: String,
                                 accountIdentifier: String): Future[BasicAccountInformation]

  def getNearby(clientId: String, clientSecret: String, latitude: BigDecimal,
                longitude: BigDecimal): Future[Seq[NearbyDetails]]
}