package io.jz.poodle

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import io.jz.poodle.Poodle.Location
import spray.http.Uri.Query

import scala.concurrent.{Await, Future}

import spray.http._
import spray.httpx.encoding.{Gzip, Deflate}
import spray.client.pipelining._

import scala.concurrent.duration._
import spray.http.HttpHeaders._
import MediaTypes._
import HttpEncodings._
import CacheDirectives._
import Poodle._

class PoodleClient(implicit system: ActorSystem) {

  import system.dispatcher

  lazy val randomUserAgent = randomUserAgentFun()

  def pipeline(userAgent: String = randomUserAgent()): HttpRequest => Future[HttpResponse] = (
    addHeaders(
      Accept(`text/html`, `application/xhtml+xml`, `image/webp`),
      `Accept-Encoding`(gzip, deflate),
      `Accept-Language`(Language("en", "US"), Language("en"), Language("pl")),
      `Cache-Control`(`max-age`(0l)),
      Connection("keep-alive"),
      Host("www.pudelek.pl"),
      Origin("http://www.pudelek.pl" :: Nil),
      `User-Agent`(userAgent)
    ) ~> addHeader("DNT", "1")
      ~> addHeader("Referer", "http://www.pudelek.pl/artykul/73200/opalam_sie_caly_czas_bo_mam_luszczyce_wygladam_szczuplej/")
      ~> logRequest(system.log)
      ~> sendReceive
      ~> decode(Deflate)
      ~> decode(Gzip)
      ~> logResponse(system.log)
    )

  def getStory(storyId: Int): Unit = {
    val uri = Uri.from(
      scheme = "http",
      host = "www.pudelek.pl",
      path = s"/artykul/$storyId")
    val request = pipeline()(Get(uri))
    implicit val timeout = Timeout(10, TimeUnit.SECONDS)
    val response = Await.result(request, 10.seconds)

    response.headers foreach {  h => println(">>> " + h) }
    println(">>> " + response.entity.asString.take(200))
  }

  def getMainPage(userAgent: () => String): Unit = {
    val uri = Uri.from(
      scheme = "http",
      host = "www.pudelek.pl")
    implicit val timeout = Timeout(10, TimeUnit.SECONDS)
    val response = Await.result(pipeline()(Get(uri)), 10.seconds)
    response.headers foreach {  h => println(">>> " + h) }
    println(">>> " + response.entity.asString.take(200))
  }

  def postComment(storyId: Int, body: String, nick: String = "gośću"): Future[Option[Poodle.Location]] = {
    //TODO content-length
    //TODO cookie
    //TODO referer
    val uri = Uri.from(
      scheme = "http",
      host = "www.pudelek.pl",
      path = "/komentarz")
    implicit val timeout = Timeout(10, TimeUnit.SECONDS)
    println("Q >>> " + query(storyId, body, nick))
    val response = Await.result(pipeline()(Post(uri, HttpEntity(`application/x-www-form-urlencoded`, query(storyId, body, nick)))), 10.seconds)

    response.headers foreach {  h => println(">>> " + h) }
    println(">>> " + response.entity.asString.take(200))
    Future.successful(None)

  }

  private def query(storyId: Int, body: String, nick: String): String = {
    Query(
      "article_comment[aid]" -> storyId.toString,
      "article_comment[sid]" -> "",
      "article_comment[response_to]" -> "",
      "article_comment[response_bucket]" -> "",
      "article_comment[txt]" -> body,
      "article_comment[aut]" -> nick
    ).toString

  }
}
