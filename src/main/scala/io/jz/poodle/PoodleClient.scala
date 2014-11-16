package io.jz.poodle

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import io.jz.poodle.Poodle.ChunkLocation
import spray.http.Uri.Query

import scala.concurrent.{Await, Future}

import spray.http._
import spray.httpx.encoding.{Gzip, Deflate}
import spray.httpx.unmarshalling.{MalformedContent, Deserialized, Deserializer, FromResponseUnmarshaller}
import spray.client.pipelining._

import scala.concurrent.duration._
import spray.http.HttpHeaders._
import MediaTypes._
import HttpEncodings._
import CacheDirectives._
import StatusCodes._
import Poodle._

class ChunkLocationUnmarshaller extends Deserializer[HttpResponse, ChunkLocation] {
  lazy val PathRegexp = """/artykul/([0-9]+)/[^/]+/([0-9]+)/""".r
  override def apply(response: HttpResponse): Deserialized[ChunkLocation] = {
    val chunkLocation = response match {
      case r: HttpResponse if r.status == Found =>
        r.header[Location] map {
          case Location(Uri(_, _, path, _, Some(commentId))) =>
            PathRegexp.findFirstIn(path.toString()) match {
              case Some(PathRegexp(storyId, commentPage)) =>
                ChunkLocation(storyId.toInt, commentPage.toInt, commentId, path.toString)
            }
        }
    }
    println ("CL >>>" + chunkLocation)
    chunkLocation match {
      case Some(cl) => Right(cl)
      case None => Left(MalformedContent("dupa"))
    }
  }
}

class PoodleClient(hostName: String = "www.pudelek.pl")(implicit system: ActorSystem) {

  import system.dispatcher

  val randomUserAgent = randomUserAgentFun()

  implicit val x = new ChunkLocationUnmarshaller

  def postComment(storyId: Int, body: String, nick: String = "gośću"): Future[Option[ChunkLocation]] = {

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
    val chunkLocation = pipeline(postRequest)
    implicit val timeout = Timeout(10, TimeUnit.SECONDS)
    val response = Await.result(chunkLocation, 10.seconds)
    Future.successful(None)
  }

  //  def getStory(storyId: Int): Unit = {
  //    val uri = Uri.from(
  //      scheme = "http",
  //      host = "www.pudelek.pl",
  //      path = s"/artykul/$storyId")
  //    val request = pipeline()(Get(uri))
  //    implicit val timeout = Timeout(10, TimeUnit.SECONDS)
  //    val response = Await.result(request, 10.seconds)
  //
  //    response.headers foreach {  h => println(">>> " + h) }
  //    println(">>> " + response.entity.asString.take(200))
  //  }
  //
  //  def getMainPage(userAgent: () => String): Unit = {
  //    val uri = Uri.from(
  //      scheme = "http",
  //      host = "www.pudelek.pl")
  //    implicit val timeout = Timeout(10, TimeUnit.SECONDS)
  //    val response = Await.result(pipeline()(Get(uri)), 10.seconds)
  //    response.headers foreach {  h => println(">>> " + h) }
  //    println(">>> " + response.entity.asString.take(200))
  //  }
}
