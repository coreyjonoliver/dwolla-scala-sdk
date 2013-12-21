package dwolla.api

object Exceptions {

  object DwollaException {
    def apply(message: String) = new DwollaException {
      def message = message
    }
  }

  trait DwollaException extends RuntimeException {
    def message: String

    override def toString = {
      this.getClass.getName + "(" +
        "message: " + message + ")"
    }
  }
}