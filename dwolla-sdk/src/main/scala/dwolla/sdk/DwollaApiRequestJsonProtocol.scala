package dwolla.sdk

import spray.json.DefaultJsonProtocol

private[sdk] object DwollaApiRequestJsonProtocol extends DefaultJsonProtocol {

  import reflect._

  override protected def extractFieldNames(classTag: ClassTag[_]) = {
    super.extractFieldNames(classTag).map {
      case "accessToken" => "oauth_token"
      case "clientId" => "client_id"
      case "clientSecret" => "client_secret"
      case n => n
    }
  }

  case class SendAsGuestRequest(clientId: String, clientSecret: String, destinationId: String, amount: BigDecimal,
    firstName: String, lastName: String, emailAddress: String, routingNumber: String,
    accountNumber: String, accountType: String, assumeCosts: Option[Boolean] = None,
  destinationType: Option[String] = None, notes: Option[String] = None, groupId: Option[Int],
  additionalFees: Seq[FacilitatorFee] = List(), facilitatorAmount: Option[BigDecimal] = None,
  assumeAdditionalFees: Option[Boolean] = None)

  case class SendRequest(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
                         destinationType: Option[String] = None,
                         facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
                         notes: Option[String] = None,
                         additionalFees: Seq[FacilitatorFee] = List(), assumeAdditionalFees: Option[Boolean] =
  None)

  case class RefundRequest(accessToken: String, pin: String, transactionId: Int, fundsSource: Int,
                           amount: BigDecimal, notes: Option[String])

  implicit val facilitatorFeeFormat = jsonFormat2(FacilitatorFee)

  implicit val sendAsGuestRequestFormat = jsonFormat17(SendAsGuestRequest)

  implicit val sendRequestFormat = jsonFormat10(SendRequest)

  implicit val refundRequestFormat = jsonFormat6(RefundRequest)
}
