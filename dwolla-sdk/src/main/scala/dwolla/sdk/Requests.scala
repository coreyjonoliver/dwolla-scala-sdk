package dwolla.sdk

private[sdk] object Requests {
  case class AddFundingSourceRequest(accessToken: String, accountNumber: String, routingNumber: String,
                                     accountType: String, name: String)

  case class DepositFundsRequest(accessToken: String, fundingId: String, pin: String, amount: BigDecimal)

  case class WithdrawFundsRequest(accessToken: String, fundingId: String, pin: String, amount: BigDecimal)

  case class SendAsGuestRequest(clientId: String, clientSecret: String, destinationId: String, amount: BigDecimal,
                                firstName: String, lastName: String, emailAddress: String, routingNumber: String,
                                accountNumber: String, accountType: String, assumeCosts: Option[Boolean] = None,
                                destinationType: Option[String] = None, notes: Option[String] = None,
                                groupId: Option[Int],
                                additionalFees: Seq[FacilitatorFee] = List(), facilitatorAmount: Option[BigDecimal] =
  None,
                                assumeAdditionalFees: Option[Boolean] = None)

  case class SendRequest(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
                         destinationType: Option[String] = None,
                         facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
                         notes: Option[String] = None,
                         additionalFees: Seq[FacilitatorFee] = List(), assumeAdditionalFees: Option[Boolean] =
  None)

  case class RefundRequest(accessToken: String, pin: String, transactionId: Int, fundsSource: Int,
                           amount: BigDecimal, notes: Option[String])
}
