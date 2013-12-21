package dwolla.api

import spray.json._

object DwollaApiJsonProtocol extends DefaultJsonProtocol {

  case class Response[T : JsonFormat](success: Boolean, message: String, response: T)

  case class FullAccountInformation(city: String,
                                    id: String,
                                    latitude: BigDecimal,
                                    longitude: BigDecimal,
                                    name: String,
                                    state: String,
                                    `type`: String)

  implicit def responseFormat[T : JsonFormat] = jsonFormat(Response.apply[T], "Success", "Message", "Response")

  implicit def fullAccountInformationFormat = jsonFormat(FullAccountInformation,
    "City",
    "Id",
    "Latitude",
    "Longitude",
    "Name",
    "State",
    "Type")
}