package dwolla.api

import akka.dispatch.Future
import dwolla.api.DwollaApiJsonProtocol.FullAccountInformation

trait DwollaApi {
  def getBalance(accessToken: String): Future[BigDecimal]
  def getFullAccountInformation(accessToken: String): Future[FullAccountInformation]
}