package io.jz.poodle

import akka.http.Http
import com.typesafe.config.{ Config, ConfigFactory }
import scala.util.{ Failure, Success }
import akka.actor.ActorSystem
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import akka.http.model._

object TestClient extends App {
  val testConf: Config = ConfigFactory.parseString("""
    akka.loglevel = INFO
    akka.log-dead-letters = off
                                                   """)
  implicit val system = ActorSystem("ServerTest", testConf)
  implicit val fm = FlowMaterializer()
  import system.dispatcher

  val host = "spray.io"

  println(s"Fetching HTTP server version of host `$host` ...")

  val connection = Http().outgoingConnection(host)
  val result = Source.singleton(HttpRequest()).via(connection.flow).runWith(Sink.head)

  result.map(_.header[headers.Server]) onComplete {
    case Success(res)   ⇒ println(s"$host is running ${res mkString ", "}")
    case Failure(error) ⇒ println(s"Error: $error")
  }
  result onComplete { _ ⇒ system.shutdown() }
}
