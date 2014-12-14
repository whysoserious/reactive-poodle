package io.jz.poodle

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import io.jz.poodle.Poodle.ChunkLocation
import org.scalatest.{Matchers, FlatSpec}
import scala.concurrent.duration._

import scala.concurrent.Await

class PoodleClientIntegrationSpec extends FlatSpec with Matchers {

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
    val cl = Await.result(poodleClient.postChunk(73200, "nono", "ania").mapTo[ChunkLocation], 10.seconds)
    println(">>> " + cl)
    1 === 1
  }
}
