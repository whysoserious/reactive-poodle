package io.jz.poodle

import akka.actor.ActorSystem
import io.jz.poodle.Poodle.{ChunkLocation, _}
import spray.client.pipelining._
import spray.http.CacheDirectives._
import spray.http.HttpEncodings._
import spray.http.HttpHeaders._
import spray.http.MediaTypes._
import spray.http.Uri.Query
import spray.http._
import spray.httpx.encoding.{Deflate, Gzip}
import spray.httpx.unmarshalling.{Deserialized, Deserializer, MalformedContent}

import scala.concurrent.Future

object PoodleClient {

  class ChunkLocationUnmarshaller extends Deserializer[HttpResponse, ChunkLocation] {

    val PathRegexp = """/artykul/([0-9]+)/[^/]+/([0-9]+)/""".r

    override def apply(response: HttpResponse): Deserialized[ChunkLocation] = {
      val chunkLocation = for {
        Location(Uri(_, _, path, _, Some(commentId))) <- response.header[Location]
        pr @ PathRegexp(storyId, commentPage) <- PathRegexp.findFirstIn(path.toString())
      } yield {
        ChunkLocation(storyId.toInt, commentPage.toInt, commentId, pr)
      }
      chunkLocation match {
        case Some(cl) => Right(cl)
        case None => Left(MalformedContent("Invalid Location header: " + response.header[Location]))
      }
    }

  }

}

class PoodleClient(hostName: String = "www.pudelek.pl")(implicit system: ActorSystem) {

  import io.jz.poodle.PoodleClient._
  import system.dispatcher

  val randomUserAgent = randomUserAgentFun()

  implicit val chunkLocationUnmarshaller = new ChunkLocationUnmarshaller

  def postComment(storyId: Int, body: String, nick: String = "gość"): Future[ChunkLocation] = {
    val pipeline: HttpRequest => Future[ChunkLocation] = (
      addHeaders(
        Accept(`text/html`, `application/xhtml+xml`, `image/webp`),
        `Accept-Encoding`(gzip, deflate),
        `Accept-Language`(Language("en", "US"), Language("en"), Language("pl")),
        `Cache-Control`(`max-age`(0l)),
        Connection("keep-alive"),
        Host(hostName),
        Origin(s"http://$hostName" :: Nil),
        `User-Agent`(randomUserAgent())
      ) ~> addHeader("DNT", "1")
        ~> addHeader("Referer", s"http://$hostName/artykul/$storyId/")
        ~> logRequest(system.log)
        ~> sendReceive
        ~> decode(Deflate)
        ~> decode(Gzip)
        ~> logResponse(system.log)
        ~> unmarshal[ChunkLocation]
      )
    val payload = Query(
      "article_comment[aid]" -> storyId.toString,
      "article_comment[sid]" -> "",
      "article_comment[response_to]" -> "",
      "article_comment[response_bucket]" -> "",
      "article_comment[txt]" -> body,
      "article_comment[aut]" -> nick).toString()
    val uri = Uri.from(
      scheme = "http",
      host = hostName,
      path = "/komentarz")
    val postRequest = Post(uri, HttpEntity(`application/x-www-form-urlencoded`, payload))
    pipeline(postRequest)
  }

}
