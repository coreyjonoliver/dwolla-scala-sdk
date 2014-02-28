package dwolla.sdk

import spray.json._
import com.github.nscala_time.time.Imports._
import org.joda.time.format.ISODateTimeFormat
import dwolla.sdk.Responses._

private[sdk] object DwollaApiResponseJsonProtocol extends CapitalizedJsonProtocol {

  implicit object JodaDateTimeFormat extends RootJsonFormat[Option[DateTime]] {
    val formatter = ISODateTimeFormat.dateTimeParser()

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