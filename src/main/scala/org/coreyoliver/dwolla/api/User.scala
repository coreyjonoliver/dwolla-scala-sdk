package org.coreyoliver.dwolla.api {
  import net.liftweb.json._
    import JsonDSL._
  import net.liftweb.common.Box

  import org.coreyoliver.dwolla._

  case class User(user_id: String, city: String, latitude: Double, longitude: Double, name: String, state: String, user_type: String) extends DwollaResource[User] {

    val meta = User
    val _id = user_id
  }

  object User extends DwollaResourceMeta[User] {
    protected def extract(json:JValue) = json.extract[User]
    protected def extractFindResults(json:JValue) = json.extract[List[User]]

    def apply()(implicit authorizationToken:Option[DwollaToken]) = query(None)
  }
}


