package org.coreyoliver.dwolla.api {
  import org.coreyoliver.dwolla._

  import net.liftweb.common._
  import net.liftweb.json._
    import JsonDSL._
    import Extraction._

  trait DwollaResource[MyType <: DwollaResource[MyType]] {
    self: MyType =>

    def meta: DwollaResourceMeta[MyType]
  }

  trait DwollaResourceMeta[Model <: DwollaResource[Model]] {
    implicit val formats : Formats = DefaultFormats

    protected val className = this.getClass.getName.split("\\.").last.dropRight(1)

    protected def resource : String = className.replaceAll("(.)([A-Z])", "$1_$2").toLowerCase + "s"
    protected def resourceIdentifier : String = resource + "_id"
    protected def extract(json:JValue) : Model
    protected def extractFindResults(json:JValue) : List[Model]

    protected def query(action:Option[String], requestBody:JValue = JObject(Nil))(implicit authorizationToken:Option[DwollaToken] = None) = {
      for {
        resultingJson <- Dwolla.executeAction(authorizationToken, resource, action, requestBody)
      } yield {
        extract(resultingJson)
      }
    }

    protected def findQuery(searchParameters:JValue)(implicit authorizationToken:Option[DwollaToken] = None) = {
      for {
        resultingJson <- Dwolla.executeAction(authorizationToken, resource, Some("find"), searchParameters)
      } yield {
        extractFindResults(resultingJson)
      }
    }

def find(id:Long)(implicit authorizationToken:Option[DwollaToken]) = query(None, (resourceIdentifier -> id))
  }

  trait ImmutableDwollaResource[MyType <: ImmutableDwollaResource[MyType, CrudResponseType], CrudResponseType] extends DwollaResource[MyType] {
    self: MyType =>

    def meta: ImmutableDwollaResourceMeta[MyType, CrudResponseType]
    def _id : Option[Long]

    def save(implicit authorizationToken:Option[DwollaToken]) = meta.save(this)
  }

  trait ImmutableDwollaResourceMeta[Model <: ImmutableDwollaResource[Model, CrudResponseType], CrudResponseType] extends DwollaResourceMeta[Model] {
    protected def extractCrudResponse(json:JValue) : CrudResponseType

    protected def resultRetrievalQuery(action:Option[String], requestBody:JValue)(implicit authorizationToken:Option[DwollaToken] = None) = {
      for {
        resultingJson <- Dwolla.executeAction(authorizationToken, resource, action, requestBody)
      } yield {
        extractCrudResponse(resultingJson)
      }
    }

    def save(instance:Model)(implicit authorizationToken:Option[DwollaToken]) : Box[CrudResponseType] = {
      instance._id match {
        case Some(_) => Failure("You can't update an immutable resource.")
        case _ => create(instance)
      }
    }

    protected def create(instance:Model)(implicit authorizationToken:Option[DwollaToken]) = {
      resultRetrievalQuery(Some("create"), decompose(instance))
    }
  }

  trait MutableDwollaResource[MyType <: MutableDwollaResource[MyType, CrudResponseType], CrudResponseType] extends ImmutableDwollaResource[MyType, CrudResponseType] {
    self: MyType =>

    def meta : MutableDwollaResourceMeta[MyType, CrudResponseType]

    def delete(implicit authorizationToken:Option[DwollaToken]) = meta.delete(this)
  }

  trait MutableDwollaResourceMeta[Model <: MutableDwollaResource[Model, CrudResponseType], CrudResponseType] extends ImmutableDwollaResourceMeta[Model, CrudResponseType] {
override def save(instance:Model)(implicit authorizationToken:Option[DwollaToken]) = {
      instance._id match {
        case Some(_) => modify(instance)
        case _ => create(instance)
      }
    }

    protected def modify(instance:Model)(implicit authorizationToken:Option[DwollaToken]) = {
      resultRetrievalQuery(Some("modify"), decompose(instance))
    }

    def delete(instance:Model)(implicit authorizationToken:Option[DwollaToken]) = {
      resultRetrievalQuery(Some("delete"), (resourceIdentifier -> instance._id))
    }
  }
}
