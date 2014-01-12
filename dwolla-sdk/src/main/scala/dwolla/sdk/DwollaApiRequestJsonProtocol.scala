package dwolla.sdk

import spray.json.DefaultJsonProtocol
import dwolla.sdk.Requests._
import scala.reflect.ClassTag

object DwollaApiRequestJsonProtocol extends DefaultJsonProtocol {

  override protected def extractFieldNames(classTag: ClassTag[_]) = {
    super.extractFieldNames(classTag).map {
      case "accessToken" => "oauth_token"
      case "clientId" => "client_id"
      case "clientSecret" => "client_secret"
      case "accountNumber" => "account_number"
      case "routingNumber" => "routing_number"
      case "accountType" => "account_type"
      case n => n
    }
  }

  implicit val addFundingSourceRequestFormat = jsonFormat5(AddFundingSourceRequest)

  implicit val depositFundsRequestFormat = jsonFormat4(DepositFundsRequest)

  implicit val facilitatorFeeFormat = jsonFormat2(FacilitatorFee)

  implicit val sendAsGuestRequestFormat = jsonFormat17(SendAsGuestRequest)

  implicit val sendRequestFormat = jsonFormat10(SendRequest)

  implicit val refundRequestFormat = jsonFormat6(RefundRequest)
}
