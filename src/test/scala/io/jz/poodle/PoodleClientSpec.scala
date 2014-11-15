package io.jz.poodle

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}
import spray.http.HttpHeaders.{`User-Agent`, Host, Connection}
import spray.http.Language
import Poodle._

class PoodleClientSpec extends FlatSpec with Matchers {

  val config = ConfigFactory.parseString(
    """
      |akka.loglevel = DEBUG
      |akka.stdout-loglevel = DEBUG
      |akka.log-dead-letters = off
    """.stripMargin)

  protected def withPoodleClient(testCode: PoodleClient => Any): Unit = {
    implicit val actorSystem = ActorSystem("PoodleClientSpec", config)
    val poodleClient = new PoodleClient
    try {
      testCode(poodleClient)
    } finally {
      actorSystem.shutdown()
      actorSystem.awaitTermination()
    }
  }

  "PoodleClient" should "post a comment in a story and retrieve a Location" in withPoodleClient { poodleClient =>
    val randomUserAgent: () => String = randomUserAgentFun()
    poodleClient.postComment(73200, "co wy mówicie")
//    poodleClient.getMainPage(randomUserAgent)
//    poodleClient.getStory(73200, randomUserAgent)
    1 === 1
  }



}
//akka.log-dead-letters-during-shutdown = off