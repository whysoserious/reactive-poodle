package io.jz.poodle

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.client.RequestBuilding._
import akka.http.model.MediaTypes._
import akka.http.model.Uri.Query
import akka.http.model._
import akka.http.model.headers.CacheDirectives._
import akka.http.model.headers.HttpEncodings._
import akka.http.model.headers._
import akka.http.unmarshalling._
import akka.http.util._
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.{Source, Sink}
import io.jz.poodle.Poodle._

import scala.concurrent.Future
import scala.util._

object PoodleClient {

  val PathRegexp = """/artykul/([0-9]+)/[^/]+/([0-9]+)/""".r

  implicit val chunkLocationUnmarshaller = Unmarshaller[HttpResponse, ChunkLocation] { response =>
    FastFuture {
      val chunkLocation = for {
        Location(Uri(_, _, path, _, Some(commentId))) <- response.header[Location]
        pr @ PathRegexp(storyId, commentPage) <- PathRegexp.findFirstIn(path.toString())
      } yield {
        ChunkLocation(storyId.toInt, commentPage.toInt, commentId, pr)
      }
      chunkLocation match {
        case Some(cl) => Success(cl)
        case None => Failure(new Exception("Invalid Location header: " + response.header[Location]))
      }
    }
  }

}

class PoodleClient(hostName: String = "www.pudelek.pl", userAgent: () => String = randomUserAgentFun())
                  (implicit system: ActorSystem) {

  import PoodleClient._
  import system.dispatcher

  implicit val fm: FlowMaterializer = FlowMaterializer()

  def postChunk(storyId: Int, body: String, nick: String = "gość"): Future[ChunkLocation] = {
    val connection = Http().outgoingConnection(hostName)
    val fu = Source.singleton(request(storyId, body, nick))
      .via(connection.flow)
      .runWith(Sink.head)
      .flatMap { Unmarshal(_).to[ChunkLocation] }
    fu onComplete {
      case x => println(">>>> " + x)
    }
    fu
  }

  protected def request(storyId: Int, body: String, nick: String): HttpRequest = {
    val applyHeaders = Function.chain(
      Seq(
        addHeader(`User-Agent`(userAgent())),
        addHeader(Accept(`text/html`, `application/xhtml+xml`, `image/webp`)),
        addHeader(`Accept-Encoding`(gzip)),
        addHeader(`Accept-Language`(Language("en", "US"), Language("en"), Language("pl"))),
        addHeader(`Cache-Control`(`max-age`(0l))),
        addHeader(Connection("keep-alive")),
        addHeader(Host(hostName)),
        addHeader(Origin(s"http://$hostName")),
        addHeader("Referer", s"http://$hostName/artykul/$storyId/"),
        addHeader("DNT", "1")
      )
    )
    val payload = query(storyId, body, nick)
    val uri = Uri.from(scheme = "http", host = hostName, path = "/komentarz")
    val postRequest = Post(uri, HttpEntity(`application/x-www-form-urlencoded`, payload))
    applyHeaders(postRequest)
  }

  protected def query(storyId: Int, body: String, nick: String): String = {
    Query(
      "article_comment[aid]" -> storyId.toString,
      "article_comment[sid]" -> "",
      "article_comment[response_to]" -> "",
      "article_comment[response_bucket]" -> "",
      "article_comment[txt]" -> body,
      "article_comment[aut]" -> nick
    ).toString()
  }

}
