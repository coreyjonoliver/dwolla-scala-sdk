package dwolla.sdk

import spray.json._
import com.github.nscala_time.time.Imports._
import org.joda.time.format.DateTimeFormatterBuilder

private[sdk] object DwollaApiResponseJsonProtocol extends CapitalizedJsonProtocol {

  // TODO: Parse fields as snakified lowercase
  case class GetAccessTokenResponse(accessToken: String)

  // TODO: Parse fields as snakified lowercase
  case class GetAccessTokenErrorResponse(error: String, errorDescription: String)

  case class Response[T: JsonFormat](success: Boolean, message: String, response: Option[T])

  case class AddFundingSourceResponse(id: String, name: String, `type`: String, verified: Boolean,
                                      processingType: String)

  case class GetFundingSourceDetailsResponse(id: String, name: String, `type`: String, verified: Boolean,
                                             processingType: String, balance: BigDecimal)

  case class DepositFundsResponse(amount: BigDecimal, date: Option[DateTime], destinationId: String,
                                  destinationName: String, id: Int, sourceId: String, sourceName: String,
                                  `type`: String, userType: String, status: String,
                                  clearingDate: Option[DateTime],
                                  notes: Option[String])

  case class ListFundingSourcesResponseElement(id: String, name: String, `type`: String, verified: Boolean,
                                               processingType: String)

  type ListFundingSourcesResponse = List[ListFundingSourcesResponseElement]

  type SendMoneyAsGuestResponse = Int

  type SendMoneyResponse = Int

  case class GetTransactionDetailsResponseFee(id: Int, amount: BigDecimal, `type`: String)

  case class GetTransactionDetailsResponse(amount: BigDecimal, date: Option[DateTime], destinationId: String,
                                           destinationName: String, id: Int, sourceId: String, sourceName: String,
                                           `type`: String, userType: String, status: String,
                                           clearingDate: Option[DateTime],
                                           notes: Option[String], fees: Option[Seq[GetTransactionDetailsResponseFee]])

  type ListAllTransactionsResponseElement = GetTransactionDetailsResponse

  type ListAllTransactionsResponse = Seq[ListAllTransactionsResponseElement]

  type GetBalanceResponse = BigDecimal

  case class FullAccountInformationResponse(city: String, id: String, latitude: BigDecimal, longitude: BigDecimal,
                                            name: String, state: String, `type`: String)

  case class BasicAccountInformationResponse(id: String, latitude: BigDecimal, longitude: BigDecimal, name: String)

  case class FindUsersNearbyResponseElement(id: String, latitude: BigDecimal, name: String, longitude: BigDecimal,
                                            delta: BigDecimal,
                                            image: String)

  type FindUsersNearbyResponse = Seq[FindUsersNearbyResponseElement]

  case class IssueRefundResponse(transactionId: Int, refundDate: Option[DateTime], amount: BigDecimal)

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