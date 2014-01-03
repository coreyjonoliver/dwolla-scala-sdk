package dwolla.sdk

import scala.concurrent.Future
import dwolla.sdk.DwollaApiResponseJsonProtocol._

private[sdk] case class FacilitatorFee(destinationId: String,
                                       amount: BigDecimal)

private[sdk] trait DwollaApi {

  def getBalance(accessToken: String): Future[BigDecimal]

  def addFundingSource(accessToken: String, accountNumber: String, routingNumber: String, accountType: String,
                       name: String): Future[AddFundingSourceResponse]

  def getFundingSourceDetails(accessToken: String, fundingId: Int): Future[GetFundingSourceDetailsResponse]

  def depositFunds(accessToken: String, fundingId: Int, pin: String, amount: BigDecimal): Future[DepositFundsResponse]

  def getTransactionDetails(accessToken: String, transactionId: Int): Future[GetTransactionDetailsResponse]

  def sendMoneyAsGuest(clientId: String, clientSecret: String, destinationId: String, amount: BigDecimal,
                  firstName: String, lastName: String, emailAddress: String, routingNumber: String,
                  accountNumber: String, accountType: String, assumeCosts: Option[Boolean] = None,
                  destinationType: Option[String] = None, notes: Option[String] = None, groupId: Option[Int],
                  additionalFees: Seq[FacilitatorFee] = List(), facilitatorAmount: Option[BigDecimal] = None,
                  assumeAdditionalFees: Option[Boolean] = None): Future[SendMoneyAsGuestResponse]

  def listAllTransactions(accessToken: String): Future[Seq[GetTransactionDetailsResponse]]


  def issueRefund(accessToken: String, pin: String, transactionId: Int, fundsSource: Int, amount: BigDecimal,
             notes: Option[String]): Future[IssueRefundResponse]

  def sendMoney(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
           destinationType: Option[String] = None,
           facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
           notes: Option[String] = None,
           additionalFees: Seq[FacilitatorFee] = List(), assumeAdditionalFees: Option[Boolean] = None):
  Future[SendMoneyResponse]

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformationResponse]

  def getBasicAccountInformation(clientId: String, clientSecret: String,
                                 accountIdentifier: String): Future[BasicAccountInformationResponse]

  def findUsersNearby(clientId: String, clientSecret: String, latitude: BigDecimal,
                longitude: BigDecimal): Future[FindUsersNearbyResponse]
}