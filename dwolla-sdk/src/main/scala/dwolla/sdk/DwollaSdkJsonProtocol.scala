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

  case class BasicAccountInformation(id: String,
                                     latitude: BigDecimal,
                                     longitude: BigDecimal,
                                     name: String)

  case class NearbyElement(id: String,
                    latitude: BigDecimal,
                    name: String,
                    longitude: BigDecimal,
                    delta: BigDecimal,
                    image: String)

  implicit def responseFormat[T: JsonFormat] = jsonFormat3(Response.apply[T])

  implicit def fullAccountInformationFormat = jsonFormat7(FullAccountInformation)

  implicit def basicAccountInformationFormat = jsonFormat4(BasicAccountInformation)

  implicit def nearbyElementFormat = jsonFormat6(NearbyElement)
}