package dwolla.sdk

import spray.json._

private[sdk] trait CapitalizedJsonProtocol extends DefaultJsonProtocol {

  import reflect._

  override protected def extractFieldNames(classTag: ClassTag[_]) = {
    super.extractFieldNames(classTag).map {
      (_).capitalize
    }
  }
}

private[sdk] object CapitalizedJsonProtocol extends CapitalizedJsonProtocol