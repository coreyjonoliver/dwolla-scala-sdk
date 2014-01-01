package dwolla.sdk

import scala.concurrent.Future
import dwolla.sdk.DwollaApiResponseJsonProtocol._

private[sdk] case class FacilitatorFee(destinationId: String,
                                       amount: BigDecimal)

private[sdk] trait DwollaApi {

  def getTransactionById(accessToken: String, transactionId: Int): Future[TransactionByIdResponse]

  def sendAsGuest(clientId: String, clientSecret: String, destinationId: String, amount: BigDecimal,
                  firstName: String, lastName: String, emailAddress: String, routingNumber: String,
                  accountNumber: String, accountType: String, assumeCosts: Option[Boolean] = None,
                  destinationType: Option[String] = None, notes: Option[String] = None, groupId: Option[Int],
                  additionalFees: Option[Seq[FacilitatorFee]] = None, facilitatorAmount: Option[BigDecimal] = None,
                  assumeAdditionalFees: Option[Boolean] = None)

  def getTransactionListing(accessToken: String): Future[Seq[TransactionByIdResponse]]


  def refund(accessToken: String, pin: String, transactionId: Int, fundsSource: Int, amount: BigDecimal,
             notes: Option[String]): Future[Refund]

  def transactionSend(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
           destinationType: Option[String] = None,
           facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
           notes: Option[String] = None,
           additionalFees: Option[Seq[FacilitatorFee]] = None, assumeAdditionalFees: Option[Boolean] = None):
  Future[Int]

  def getBalance(accessToken: String): Future[BigDecimal]

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformation]

  def getBasicAccountInformation(clientId: String, clientSecret: String,
                                 accountIdentifier: String): Future[BasicAccountInformation]

  def getNearby(clientId: String, clientSecret: String, latitude: BigDecimal,
                longitude: BigDecimal): Future[Seq[NearbyDetails]]
}