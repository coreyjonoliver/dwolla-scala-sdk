package dwolla.sdk

import Responses._

private[sdk] object DwollaApiAuthResponseJsonProtocol extends SnakifiedJsonProtocol {
  implicit val getAccessTokenResponseFormat = jsonFormat1(GetAccessTokenResponse)

  implicit val getAccessTokenErrorResponse = jsonFormat2(GetAccessTokenErrorResponse)
}
