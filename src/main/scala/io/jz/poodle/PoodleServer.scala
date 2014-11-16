package io.jz.poodle

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import spray.routing.{Route, SimpleRoutingApp}

object PoodleServer extends App with SimpleRoutingApp {

  val config = ConfigFactory.parseString(
    """
      |akka.loglevel = DEBUG
      |akka.stdout-loglevel = DEBUG
      |akka.log-dead-letters = off
    """.stripMargin)

  implicit val actorSystem = ActorSystem("PoodleServer", config)

  lazy val indexRoute = {
    get {
      path("") {
        complete {
          <html>
            <form action="/file-upload" method="post" enctype="multipart/form-data">
              <input type="file" name="userfile" />
              <button name="upload" type="submit" value="Submit" />
            </form>
          </html>
        }
      }
    }
  }

  lazy val uploadRoute = {
    post {
      path("file-upload") {
        logRequest("file-upload") {
          complete {
            "ok"
          }
        }
      }
    }
  }

  startServer(interface = "localhost", port = 8080) {
    indexRoute ~ uploadRoute
  }

}






