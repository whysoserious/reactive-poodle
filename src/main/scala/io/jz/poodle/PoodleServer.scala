//package io.jz.poodle
//
//import akka.actor.ActorSystem
//import com.typesafe.config.ConfigFactory
//
//object PoodleServer extends App with SimpleRoutingApp {
//
//  //TODO save or guess Content-type
//  //Header Chunk must contain Contente-Type and filename
//  //  Server: Microsoft-IIS/8.5
//  //  X-Powered-By: ASP.NET
//  //  X-Powered-By: ARR/2.5
//  // get / path
//
//  val config = ConfigFactory.parseString(
//    """
//      |akka.loglevel = DEBUG
//      |akka.stdout-loglevel = DEBUG
//      |akka.log-dead-letters = off
//      |spray.can.server.request-chunk-aggregation-limit = 0
//    """.stripMargin)
//
//  implicit val actorSystem = ActorSystem("PoodleServer", config)
//
//  lazy val staticRoute = {
//    get {
//      path("") {
//        getFromResource("index.html")
//      } ~
//      path("jquery.js") {
//        getFromResource("jquery-2.1.1.min.js")
//      } ~
//      path("file-upload.js") {
//        getFromResource("file-upload.js")
//      }
//    }
//  }
//
//  lazy val uploadRoute = {
//    put {
//      path("file-upload") {
//        logRequest("file-upload") {
//          ctx =>
//            val contentType = ctx.request.header[`Content-Type`]
//            val httpData = ctx.request.entity
//            ctx.complete(OK)
//        }
//      }
//    }
//  }
//
//  startServer(interface = "localhost", port = 8080) {
//    staticRoute ~ uploadRoute
//  }
//
//}
//
//
//
//
//
//
