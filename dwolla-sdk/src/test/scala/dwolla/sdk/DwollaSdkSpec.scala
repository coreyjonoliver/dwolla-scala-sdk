package dwolla.sdk

import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import akka.util.Timeout
import akka.actor._
import scala.concurrent.duration._
import org.scalacheck.Prop.forAll
import org.specs2.ScalaCheck
import scala.concurrent.ExecutionContext
import org.scalacheck.Gen

class DwollaSdkSpec extends Specification with ScalaCheck with NoTimeConversions {
  implicit val system = ActorSystem()
  implicit val timeout: Timeout = 1.minutes
  implicit val ec = ExecutionContext.global

  "A DwollaSdk" should {
    "properly create an authentication URL with no redirect URI" in {
      val dwollaSdk = new DwollaSdk()

      forAll(Gen.alphaStr, Gen.listOf(Gen.alphaStr)) {
        (clientId, scopes) =>
          val expectedScopes = scopes.mkString("%7C")
          dwollaSdk.AuthenticationUrl.create(clientId, scopes,
            None) must beEqualTo(s"/oauth/v2/authenticate?client_id=$clientId&response_type=code&scope=$expectedScopes")
      }
    }

    "properly create an authentication URL with a redirect URI" in {
      val dwollaSdk = new DwollaSdk()

      val url: Gen[String] = for {
        protocol <- Gen.oneOf("http://")
        name <- Gen.alphaStr
        domain <- Gen.oneOf(".com", ".net", ".org")
      } yield protocol + name + domain

      forAll(Gen.alphaStr, Gen.listOf(Gen.alphaStr), url) {
        (clientId, scopes, redirectUri) =>
          val expectedScopes = scopes.mkString("%7C")
          dwollaSdk.AuthenticationUrl.create(clientId, scopes,
            Some(redirectUri)) must beEqualTo(s"/oauth/v2/authenticate?client_id=$clientId&response_type=code&scope" +
            s"=$expectedScopes&redirect_uri=$redirectUri")
      }
    }
  }
}