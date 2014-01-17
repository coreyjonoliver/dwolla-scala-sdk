package dwolla.sdk

import spray.json.JsonFormat
import com.github.nscala_time.time.Imports._


private[sdk] object Responses {
  case class GetAccessTokenResponse(accessToken: String)

  case class GetAccessTokenErrorResponse(error: String, errorDescription: String)

  case class Response[T: JsonFormat](success: Boolean, message: String, response: Option[T])

  case class AddFundingSourceResponse(id: String, name: String, `type`: String, verified: Boolean,
                                      processingType: String)

  case class GetFundingSourceDetailsResponse(id: String, name: String, `type`: String, verified: Boolean,
                                             processingType: String, balance: Option[BigDecimal])

  case class DepositFundsResponse(amount: BigDecimal, date: Option[DateTime], destinationId: String,
                                  destinationName: String, id: Int, sourceId: String, sourceName: String,
                                  `type`: String, userType: String, status: String,
                                  clearingDate: Option[DateTime],
                                  notes: Option[String])

  case class ListFundingSourcesResponseElement(id: String, name: String, `type`: String, verified: Boolean,
                                               processingType: String)

  type ListFundingSourcesResponse = List[ListFundingSourcesResponseElement]

  type WithdrawFundsResponse = DepositFundsResponse

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
}
