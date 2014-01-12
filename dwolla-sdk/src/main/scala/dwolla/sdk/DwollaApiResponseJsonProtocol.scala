package dwolla.sdk

import spray.json._
import com.github.nscala_time.time.Imports._
import org.joda.time.format.DateTimeFormatterBuilder
import dwolla.sdk.Responses._
import dwolla.sdk.Requests._

object DwollaApiResponseJsonProtocol extends CapitalizedJsonProtocol {

  implicit object JodaDateTimeFormat extends RootJsonFormat[Option[DateTime]] {
    val formatter = new DateTimeFormatterBuilder().appendPattern("MM/dd/yyyy").appendOptional(DateTimeFormat
      .forPattern(" HH:mm:ss").getParser()).toFormatter()

    def write(obj: Option[DateTime]): JsValue = {
      obj match {
        case Some(date) => JsString(formatter.print(date))
        case None => JsNull
      }
    }

    def read(json: JsValue): Option[DateTime] = {
      json match {
        case JsString("") => None // some DateTime fields contain an empty string when they should contain null
        case JsString(date) => Some(formatter.parseDateTime(date))
        case _ => throw new DeserializationException("DateTime expected")
      }
    }
  }

  implicit def getAccessTokenResponse = jsonFormat1(GetAccessTokenResponse)

  implicit def getAccessTokenErrorResponse = jsonFormat2(GetAccessTokenErrorResponse)

  implicit def responseFormat[T: JsonFormat] = jsonFormat3(Response.apply[T])

  implicit def addFundingSourceResponseFormat = jsonFormat5(AddFundingSourceResponse)

  implicit def getFundingSourceDetailsResponseFormat = jsonFormat6(GetFundingSourceDetailsResponse)

  implicit def depositFundsResponseFormat = jsonFormat12(DepositFundsResponse)

  implicit def listFundingSourcesResponseElementFormat = jsonFormat5(ListFundingSourcesResponseElement)

  implicit def getTransactionDetailsResponseFeeFormat = jsonFormat3(GetTransactionDetailsResponseFee)

  implicit def getTransactionDetailsResponseFormat = jsonFormat13(GetTransactionDetailsResponse)

  implicit def fullAccountInformationResponseFormat = jsonFormat7(FullAccountInformationResponse)

  implicit def basicAccountInformationResponseFormat = jsonFormat4(BasicAccountInformationResponse)

  implicit def findUsersNearbyResponseElementFormat = jsonFormat6(FindUsersNearbyResponseElement)

  implicit def issueRefundResponseFormat = jsonFormat3(IssueRefundResponse)
}