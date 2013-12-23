package dwolla.sdk

import scala.concurrent.Future
import dwolla.sdk.DwollaSdkJsonProtocol._

private[sdk] trait DwollaSdk {
  def getBalance(accessToken: String): Future[BigDecimal]

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformation]

  def getBasicAccountInformation(clientId: String, clientSecret: String,
                                 accountIdentifier: String): Future[BasicAccountInformation]
}