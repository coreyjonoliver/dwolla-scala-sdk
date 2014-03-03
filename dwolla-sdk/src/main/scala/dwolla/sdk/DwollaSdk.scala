package dwolla.sdk

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import dwolla.sdk.Responses._
import scala.language.implicitConversions
import scala.Some
import spray.http.Uri
import dwolla.sdk.Models.Fee

object AccountType extends Enumeration {
  type AccountType = Value
  val Checking, Savings = Value
}

class DwollaSdk(settings: Option[DwollaApiSettings] = None)(
  implicit system: ActorSystem,
  timeout: Timeout,
  ec: ExecutionContext) {

  private val dwollaApi = new SprayClientDwollaApi(settings)

  import AccountType._
  object Mappings {
    import dwolla.sdk.Requests.{AccountType => RequestAccountType}

    implicit def accountType2AccountType(accountType: AccountType): RequestAccountType.AccountType = {
      accountType match {
        case AccountType.Checking => RequestAccountType.Checking
        case AccountType.Savings => RequestAccountType.Savings
      }
    }

    implicit def getTransactionDetailsResponse2Transaction(response:
                                                           GetTransactionDetailsResponse): Models.Transaction = {
      val fees = response.fees.getOrElse(List()).map(f => Fee(f.id, f.amount, f.`type`))
      Models.Transaction(response.amount, response.date,
        response.destinationId,
        response.destinationName, response.id,
        Some(response.sourceId),
        Some(response.sourceName), response.`type`,
        response.userType,
        response.status, response.clearingDate,
        response.notes, fees)
    }

    implicit def addFundingSourceResponse2FundingSource(response: AddFundingSourceResponse): Models.FundingSource = {
      Models.FundingSource(response.id, response.name, response.`type`, response.verified, response.processingType)
    }

    implicit def getFundingSourceDetailsResponse2FundingSource(response: GetFundingSourceDetailsResponse):
    Models.FundingSource = {
      Models.FundingSource(response.id, response.name, response.`type`, response.verified, response.processingType)
    }

    implicit def listAllTransactionsResponse2TransactionSeq(response: ListAllTransactionsResponse): Seq[Models.Transaction] = {
      val listAllTransactionsResponseElement2Transaction = getTransactionDetailsResponse2Transaction _
      response.map(listAllTransactionsResponseElement2Transaction)
    }

    implicit def depositFundsResponse2Transaction(response: DepositFundsResponse): Models.Transaction = {
      Models.Transaction(response.amount, response.date,
        response.destinationId,
        response.destinationName, response.id,
        Some(response.sourceId),
        Some(response.sourceName), response.`type`,
        response.userType,
        response.status, response.clearingDate,
        response.notes, List())
    }

    implicit def fundingSourcesListingResponseElement2FundingSource(response: ListFundingSourcesResponseElement):
    Models.FundingSource = {
      Models.FundingSource(response.id, response.name, response.`type`, response.verified, response.processingType)
    }

    implicit def fundingSourcesListingResponse2FundingSourceList(response: ListFundingSourcesResponse):
    List[Models.FundingSource] = {
      response.map(fundingSourcesListingResponseElement2FundingSource)
    }

    implicit def basicAccountInformationResponse2User(response: BasicAccountInformationResponse): Models.User = {
      Models.User(response.id, response.latitude, response.longitude, response.name, None, None, None, None)
    }

    implicit def fullAccountInformationResponse2User(response: FullAccountInformationResponse): Models.User = {
      Models.User(response.id, response.latitude, response.longitude, response.name, Some(response.city),
      Some(response.state), Some(response.`type`), None)
    }

    implicit def findUsersNearbyResponse2UserSeq(response: FindUsersNearbyResponse): Seq[Models.User] = {
      response.map(x => Models.User(x.id, x.latitude, x.longitude, x.name, None, None, None, Some(x.image)))
    }
  }

  object AuthenticationUrl {
    def create(clientId: String, scopes: Seq[String], redirectUri: Option[String] = None) = {
      val uri = Uri("/oauth/v2/authenticate")
      val queryMap = Map("client_id" -> clientId,
        "response_type" -> "code",
        "scope" -> scopes.mkString("|"))
      if (redirectUri.isDefined) uri.withQuery(queryMap ++ Map("redirect_uri" -> redirectUri.get)).toString()
      else uri.withQuery(queryMap).toString()
    }
  }

  object AccessToken {
    def create(clientId: String, clientSecret: String, code: String, redirectUri: Option[String] = None) = {
      for {
        tokenResponse <- dwollaApi.getAccessToken(clientId, clientSecret, code, redirectUri)
      } yield tokenResponse
    }
  }

  object Balance {
    def retrieve(accessToken: String): Future[Models.Balance] = {
      for {
        balanceResponse <- dwollaApi.getBalance(accessToken)
      } yield balanceResponse
    }
  }

  object FundingSource {
    import Mappings._

    def create(accessToken: String, accountNumber: String, routingNumber: String, accountType: AccountType,
               name: String): Future[Models.FundingSource] = {
      for {
        addFundingSourceResponse <- dwollaApi.addFundingSource(accessToken, accountNumber, routingNumber,
          accountType, name)
      } yield addFundingSourceResponse
    }

    @deprecated("Use the DwollaSdk.FundingSource.create overload which provides an AccountType parameter", "1.1.1")
    def create(accessToken: String, accountNumber: String, routingNumber: String, accountType: String,
               name: String): Future[Models.FundingSource] = {
      for {
        addFundingSourceResponse <- dwollaApi.addFundingSource(accessToken, accountNumber, routingNumber,
          accountType, name)
      } yield addFundingSourceResponse
    }

    def retrieve(accessToken: String, id: String): Future[Models.FundingSource] = {
      for {
        getFundingSourceDetailsResponse <- dwollaApi.getFundingSourceDetails(accessToken, id)
      } yield getFundingSourceDetailsResponse
    }

    def all(accessToken: String, destinationId: Option[String] = None,
            destinationType: Option[String] = None): Future[Seq[Models.FundingSource]] = {
      for {
        fundingSourcesListingResponse <- dwollaApi.listFundingSources(accessToken, destinationId, destinationType)
      } yield fundingSourcesListingResponse
    }
  }

  object Transaction {
    import Mappings._

    def create(accessToken: String, pin: String, destinationId: String, amount: BigDecimal,
               destinationType: Option[String] = None,
               facilitatorAmount: Option[BigDecimal] = None, assumeCosts: Option[Boolean] = None,
               notes: Option[String] = None,
               additionalFees: Seq[FacilitatorFee] = List(), assumeAdditionalFees: Option[Boolean] = None):
    Future[Models.Transaction] = {
      for {
        transactionSendResponse <- dwollaApi.sendMoney(accessToken, pin, destinationId, amount, destinationType,
          facilitatorAmount,
          assumeCosts, notes, additionalFees, assumeAdditionalFees)
        transactionFuture <- retrieve(accessToken, transactionSendResponse)
      } yield transactionFuture
    }

    def retrieve(accessToken: String, id: Int): Future[Models.Transaction] = {
      for {
        transactionByIdResponse <- dwollaApi.getTransactionDetails(accessToken, id)
      } yield transactionByIdResponse
    }

    def all(accessToken: String, sinceDate: Option[String] = None, endDate: Option[String] = None,
            types: Option[String] = None, limit: Option[Int] = None, skip: Option[Int] = None,
            groupId: Option[String] = None): Future[Seq[Models.Transaction]] = {
      for {
        transactionListingResponse <- dwollaApi.listAllTransactions(accessToken, sinceDate, endDate, types, limit, skip, groupId)
      } yield transactionListingResponse
    }

    def deposit(accessToken: String, fundingId: String, pin: String, amount: BigDecimal): Future[Models.Transaction] = {
      for {
        depositFundsResponse <- dwollaApi.depositFunds(accessToken, fundingId, pin, amount)
      } yield depositFundsResponse
    }

    def withdraw(accessToken: String, fundingId: String, pin: String, amount: BigDecimal): Future[Models.Transaction] = {
      for {
        withdrawFundsResponse <- dwollaApi.withdrawFunds(accessToken, fundingId, pin, amount)
      } yield withdrawFundsResponse
    }
  }

  object User {
    import Mappings._

    def retrieve(clientId: String, clientSecret: String, accountIdentifier: String): Future[Models.User] = {
      for {
        basicAccountInformationResponse <- dwollaApi.getBasicAccountInformation(clientId, clientSecret,
          accountIdentifier)
      } yield basicAccountInformationResponse
    }

    def retrieve(accessToken: String): Future[Models.User] = {
      for {
        fullAccountInformationResponse <- dwollaApi.getFullAccountInformation(accessToken)
      } yield fullAccountInformationResponse
    }

    def nearby(clientId: String, clientSecret: String, latitude: BigDecimal,
               longitude: BigDecimal): Future[Seq[Models.User]] = {
      for {
        findUsersNearbyResponse <- dwollaApi.findUsersNearby(clientId, clientSecret, latitude, longitude)
      } yield findUsersNearbyResponse
    }
  }
}
