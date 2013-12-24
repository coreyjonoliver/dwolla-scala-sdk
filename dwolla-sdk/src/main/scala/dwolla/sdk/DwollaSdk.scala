package dwolla.sdk

import scala.concurrent.Future
import dwolla.sdk.DwollaSdkJsonProtocol._

private[sdk] trait DwollaSdk {
  def getTransactionDetails(accessToken: String, transactionId: Int): Future[TransactionDetails]

  def getAllTransactions(accessToken: String): Future[Seq[TransactionDetails]]

  def send(accessToken: String, pin: Int, destinationId: String, amount: BigDecimal): Future[Int]

  def getBalance(accessToken: String): Future[BigDecimal]

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformation]

  def getBasicAccountInformation(clientId: String, clientSecret: String,
                                 accountIdentifier: String): Future[BasicAccountInformation]

  def getNearby(clientId: String, clientSecret: String, latitude: BigDecimal,
                longitude: BigDecimal): Future[Seq[NearbyDetails]]
}