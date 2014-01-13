package dwolla.sdk

import spray.json._

private[sdk] trait SnakifiedJsonProtocol extends DefaultJsonProtocol {

  import reflect._

  def snakify(name: String) = name.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z\\d])([A-Z])",
    "$1_$2").toLowerCase

  override protected def extractFieldNames(classTag: ClassTag[_]) = {
    super.extractFieldNames(classTag).map {
      snakify
    }
  }
}
