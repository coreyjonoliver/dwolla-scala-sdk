package dwolla.sdk

import spray.json.DefaultJsonProtocol

private[sdk] object DwollaSdkRequestJsonProtocol extends DefaultJsonProtocol {

  case class SendRequest(oauth_token: String, pin: String, destinationId: String, amount: BigDecimal,
                         destinationType: Option[String] = None,
                         facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
                         notes: Option[String] = None,
                         additionalFees: Option[Seq[FacilitatorFee]] = None, assumeAdditionalFees: Option[Boolean] = None)

  implicit val facilitatorFeeFormat = jsonFormat2(FacilitatorFee)

  implicit val sendRequestFormat = jsonFormat10(SendRequest)
}
