package dwolla.sdk

import scala.concurrent.Future
import dwolla.sdk.DwollaSdkJsonProtocol.FullAccountInformation

trait DwollaSdk {
  def getBalance()(implicit accessToken: String): Future[BigDecimal]

  def getFullAccountInformation()(implicit accessToken: String): Future[FullAccountInformation]
}