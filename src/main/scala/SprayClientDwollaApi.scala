package dwolla.api

import akka.dispatch.Future
import akka.actor.ActorRef
import spray.client.HttpConduit
import HttpConduit._
import spray.http._
import grizzled.slf4j.Logging
//import spray.json._
//import DefaultJsonProtocol._
import DwollaApiJsonProtocol._
import spray.json._

class SprayClientDwollaApi(conduit: ActorRef) extends DwollaApi with Logging {

  def getBalance(accessToken: String): Future[BigDecimal] = {
    val pipeline: HttpRequest => Future[BigDecimal] = (
      addHeader("Accept", "application/json")
        ~> sendReceive(conduit)
        ~> mapResponse[BigDecimal]
      )
    pipeline(Get("/oauth/rest/balance/?oauth_token=%s" format accessToken))
  }

  def getFullAccountInformation(accessToken: String): Future[FullAccountInformation] = {
    val pipeline: HttpRequest => Future[FullAccountInformation] = (
      addHeader("Accept", "application/json")
      ~> sendReceive(conduit)
      ~> mapResponse[FullAccountInformation]
      )
    pipeline(Get("/oauth/rest/users/?oauth_token=%s" format accessToken))
  }

  def mapResponse[T : JsonFormat](response: HttpResponse) = {
    response.entity.asString.asJson.convertTo[Response[T]].response
  }
}
