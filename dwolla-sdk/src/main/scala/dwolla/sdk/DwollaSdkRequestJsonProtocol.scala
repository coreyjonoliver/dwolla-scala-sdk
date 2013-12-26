package dwolla.sdk

import spray.json.DefaultJsonProtocol

private[sdk] object DwollaSdkRequestJsonProtocol extends DefaultJsonProtocol {

  import reflect._

  override protected def extractFieldNames(classTag: ClassTag[_]) = {
    super.extractFieldNames(classTag).map {
      name => if (name == "accessToken") "oauth_token" else name
    }
  }

  case class SendRequest(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
                         destinationType: Option[String] = None,
                         facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
                         notes: Option[String] = None,
                         additionalFees: Option[Seq[FacilitatorFee]] = None, assumeAdditionalFees: Option[Boolean] =
  None)

  case class RefundRequest(accessToken: String, pin: String, transactionId: Int, fundsSource: Int,
                           amount: BigDecimal, notes: Option[String])

  implicit val facilitatorFeeFormat = jsonFormat2(FacilitatorFee)

  implicit val sendRequestFormat = jsonFormat10(SendRequest)

  implicit val refundRequestFormat = jsonFormat6(RefundRequest)
}
