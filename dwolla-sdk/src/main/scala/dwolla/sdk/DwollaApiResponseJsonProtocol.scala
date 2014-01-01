package dwolla.sdk

import spray.json._
import com.github.nscala_time.time.Imports._

private[sdk] object DwollaApiResponseJsonProtocol extends CapitalizedJsonProtocol {

  case class Response[T: JsonFormat](success: Boolean, message: String, response: Option[T])

  case class FeeDetails(id: Int, amount: BigDecimal, `type`: String)

  case class TransactionDetails(amount: BigDecimal, date: Option[DateTime], destinationId: String,
                                destinationName: String, id: Int, sourceId: String, sourceName: String,
                                `type`: String, userType: String, status: String, clearingDate: Option[DateTime],
                                notes: String, fees: Option[Seq[FeeDetails]])

  case class FullAccountInformation(city: String, id: String, latitude: BigDecimal, longitude: BigDecimal,
                                    name: String, state: String, `type`: String)

  case class BasicAccountInformation(id: String, latitude: BigDecimal, longitude: BigDecimal, name: String)

  case class NearbyDetails(id: String, latitude: BigDecimal, name: String, longitude: BigDecimal, delta: BigDecimal,
                           image: String)

  case class Refund(transactionId: Int, refundDate: Option[DateTime], amount: BigDecimal)

  implicit object JodaDateTimeFormat extends RootJsonFormat[Option[DateTime]] {
    def dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")

    def write(obj: Option[DateTime]): JsValue = {
      obj match {
        case Some(date) => JsString(dateTimeFormatter.print(date))
        case None => JsNull
      }
    }

    def read(json: JsValue): Option[DateTime] = {
      json match {
        case JsString("") => None // some DateTime fields contain an empty string when they should contain null
        case JsString(date) => Some(dateTimeFormatter.parseDateTime(date))
        case _ => throw new DeserializationException("DateTime expected")
      }
    }
  }

  implicit def responseFormat[T: JsonFormat] = jsonFormat3(Response.apply[T])

  implicit def feeDetailsFormat = jsonFormat3(FeeDetails)

  implicit def transactionDetailsFormat = jsonFormat13(TransactionDetails)

  implicit def fullAccountInformationFormat = jsonFormat7(FullAccountInformation)

  implicit def basicAccountInformationFormat = jsonFormat4(BasicAccountInformation)

  implicit def nearbyDetailsFormat = jsonFormat6(NearbyDetails)

  implicit def refundFormat = jsonFormat3(Refund)
}