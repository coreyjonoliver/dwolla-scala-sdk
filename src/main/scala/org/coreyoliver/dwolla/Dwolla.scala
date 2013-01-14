package org.coreyoliver.dwolla {
  import net.liftweb.common._
  import net.liftweb.util._
  import Helpers._
  import net.liftweb.json._
  import Extraction._
  import JsonDSL._
  import dispatch._

  import com.ning.http.client.{Request, RequestBuilder, Response}

  case class DwollaError(error:String, error_description:String) {
    override def toString = error + ": " + error_description
  }

  case class DwollaToken(access_token: String, token_type: String) {
    val httpHeader = token_type.toLowerCase.capitalize + " " + access_token
  }

  case class DwollaResponse(code:Int, json:JValue)

  object Dwolla extends DwollaImpl

  trait DwollaImpl {
    implicit val formats = DefaultFormats

    private[dwolla] val clientId = Props.get("dwolla.clientId") ?~! "dwolla.clientId property is required."
    private[dwolla] val clientSecret = Props.get("dwolla.clientSecret") ?~! "dwolla.clientSecret property is required."

    protected val apiUserAgent = Props.get("dwolla.userAgent") ?~! "dwolla.userAgent property is required."

    protected val apiEndpointBase = Props.get("dwolla.apiEndPointBase") openOr "https://www.dwolla.com"

    val oauthPermissions = Props.get("dwolla.oauthPermissions") ?~! "dwolla.oauthPermissions property is required."

    protected object AsDwollaResponse extends (Response => DwollaResponse) {
      def apply(r:Response) = {
        DwollaResponse(
          r.getStatusCode(),
          as.lift.Json(r)
        )
      }
    }

     protected def defaultHeaders = {
      for {
        apiUserAgent <- apiUserAgent
      } yield {
        Map("User-Agent" -> apiUserAgent, "Content-Type" -> "application/json")
      }
    }

    val authorizeUrl = {
      for {
        clientId <- clientId
        oauthPermissions <- oauthPermissions
      } yield {
        val oauth_url = host(apiEndpointBase) / "oauth" / "v2" / "authenticate" <<?
        Map("client_id" -> clientId, "response_type" -> "code", "scope" -> oauthPermissions)

        oauth_url.secure.build.getRawUrl
      }
    }

    protected def responseForRequest[T](request:RequestBuilder, handler:(JValue)=>T) = {
      // Run the query and then transform that into a Dwolla Response.
      val response = Http(request > AsDwollaResponse).either

      // Force the Promise to materialize, blocking this thread if need be.
      // We may be able to delay the manifestation of this Promise.
      response() match {
        case Right(DwollaResponse(200, json)) => Full(handler(json))

        case Right(DwollaResponse(code, json)) =>
          val error =
            {
              tryo(json.extract[DwollaError])
            } openOr {
              "Dwolla returned a " + code + " without valid JSON."
            }

          ParamFailure(error.toString, Empty, Empty, error)

        case Left(error) =>
          Failure("Error from dispatch: " + error)
      }
    }

    def retrieveToken(oauthCode:String): Box[DwollaToken] = {
      def doRequest(clientId:String, clientSecret:String, defaultHeaders:Map[String, String]) = {
        val requestBody : String = compact(render(
          ("client_id" -> clientId) ~
            ("client_secret" -> clientSecret) ~
            ("grant_type" -> "authorization_code") ~
            ("code" -> oauthCode)
        ))

        val tokenRequest = (host(apiEndpointBase) / "oauth" / "v2" / "token" <:< defaultHeaders << requestBody).secure
        responseForRequest[DwollaToken](tokenRequest, (json) => json.extract[DwollaToken])
      }

      for {
        clientId <- clientId
        clientSecret <- clientSecret
        defaultHeaders <- defaultHeaders
        result <- doRequest(clientId, clientSecret, defaultHeaders)
      } yield {
        result
      }
    }

    def executeAction(accessToken:Option[DwollaToken], module:String, action:Option[String], requestJson:JValue = JObject(Nil)) : Box[JValue] = {
      def doRequest(defaultHeaders:Map[String, String]) = {
        val requestTarget = action.toList.foldLeft(host(apiEndpointBase) / module)(_ / _).secure
        val requestBody = compact(render(requestJson))
        val headers = accessToken.map { token =>
          Map("Authorization" -> token.httpHeader)
        }.toList.foldLeft(defaultHeaders)(_ ++ _)

        val request = {
          requestJson match {
            case JObject(Nil) =>
              requestTarget <:< headers
            case _ =>
              requestTarget <:< headers << requestBody
          }
        }
        responseForRequest[JValue](request, (json) => json)
      }

      for {
        defaultHeaders <- defaultHeaders
        result <- doRequest(defaultHeaders)
      } yield {
        result
      }
    }
  }
}
