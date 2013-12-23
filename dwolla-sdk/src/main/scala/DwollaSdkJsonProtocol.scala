package dwolla.sdk

import spray.json._

private[sdk] object DwollaSdkJsonProtocol extends CapitalizedJsonProtocol {

  case class Response[T: JsonFormat](success: Boolean, message: String, response: Option[T])

  case class FullAccountInformation(city: String,
                                    id: String,
                                    latitude: BigDecimal,
                                    longitude: BigDecimal,
                                    name: String,
                                    state: String,
                                    `type`: String)

  implicit def responseFormat[T: JsonFormat] = jsonFormat3(Response.apply[T])

  implicit def fullAccountInformationFormat = jsonFormat7(FullAccountInformation)
}