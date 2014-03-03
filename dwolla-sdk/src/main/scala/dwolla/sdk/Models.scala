package dwolla.sdk

import com.github.nscala_time.time.Imports._

object Models {
    type AuthenticationUrl = String

    type AccessToken = String

    type Balance = BigDecimal

    case class Fee(id: Int, amount: BigDecimal, `type`: String)

    case class FundingSource(id: String, name: String, `type`: String, verified: Boolean, processingType: String)

    case class Transaction(amount: BigDecimal, date: Option[DateTime], destinationId: String,
                           destinationName: String, id: Int, sourceId: Option[String], sourceName: Option[String],
                           `type`: String, userType: String, status: String, clearingDate: Option[DateTime],
                           notes: Option[String], fees: Seq[Fee])

    case class User(id: String, latitude: BigDecimal, longitude: BigDecimal, name: String, city: Option[String],
                    state: Option[String], `type`: Option[String], image: Option[String])
}