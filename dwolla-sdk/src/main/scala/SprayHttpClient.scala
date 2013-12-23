package dwolla.sdk

import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import scala.concurrent.{Future, ExecutionContext}
import spray.can.Http
import spray.can.Http.HostConnectorSetup
import spray.client.pipelining._
import akka.pattern.ask

protected trait SprayHttpClient {
  def pipeline(setup: HostConnectorSetup)(
    implicit system: ActorSystem,
    timeout: Timeout,
    ec: ExecutionContext): Future[SendReceive] =
    for (
      Http.HostConnectorInfo(connector, _) <- IO(Http) ? setup
    )
    yield sendReceive(connector)
}
