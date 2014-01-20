package dwolla.sdk

import spray.json.{JsValue, JsString, JsonFormat, DefaultJsonProtocol}
import dwolla.sdk.Requests._
import dwolla.sdk.Requests.AccountType._
import scala.reflect.ClassTag

private[sdk] object DwollaApiRequestJsonProtocol extends DefaultJsonProtocol {

  override protected def extractFieldNames(classTag: ClassTag[_]) = {
    super.extractFieldNames(classTag).map {
      case "accessToken" => "oauth_token"
      case "clientId" => "client_id"
      case "clientSecret" => "client_secret"
      case "accountNumber" => "account_number"
      case "routingNumber" => "routing_number"
      case "accountType" => "account_type"
      case "fundingId" => "funding_id"
      case n => n
    }
  }

  implicit val accountTypeFormat = new JsonFormat[AccountType] {
    def write(accountType: AccountType) = accountType match {
      case Requests.AccountType.Checking => JsString("Checking")
      case Requests.AccountType.Savings => JsString("Savings")
    }

    def read(value: JsValue) = ???
  }

  implicit val addFundingSourceRequest2Format = jsonFormat5(AddFundingSourceRequest2)

  implicit val addFundingSourceRequestFormat = jsonFormat5(AddFundingSourceRequest)

  implicit val depositFundsRequestFormat = jsonFormat4(DepositFundsRequest)

  implicit val withdrawFundsRequest = jsonFormat4(WithdrawFundsRequest)

  implicit val facilitatorFeeFormat = jsonFormat2(FacilitatorFee)

  implicit val sendAsGuestRequestFormat = jsonFormat17(SendAsGuestRequest)

  implicit val sendRequestFormat = jsonFormat10(SendRequest)

  implicit val refundRequestFormat = jsonFormat6(RefundRequest)
}
