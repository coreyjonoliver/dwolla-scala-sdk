package org.coreyoliver.dwolla {
  import org.scalatest.FunSpec
  import net.liftweb.common._

  class DwollaSpec extends FunSpec {
    describe("The Dwolla Singleton") {
      it("should yield a valid authorization URL") {
        val authorizeUrl = Dwolla.authorizeUrl

        assert(authorizeUrl match {
          case Full(url) => true
          case _ => false
        })
      }
    }
  }
}

