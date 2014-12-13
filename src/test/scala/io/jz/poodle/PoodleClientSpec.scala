//package io.jz.poodle
//
//import akka.actor.ActorSystem
//import com.typesafe.config.ConfigFactory
//import org.scalatest.{FlatSpec, Matchers}
//import Poodle._
//
//class PoodleClientSpec extends FlatSpec with Matchers {
//
//  val config = ConfigFactory.parseString(
//    """
//      |akka.loglevel = DEBUG
//      |akka.stdout-loglevel = DEBUG
//      |akka.log-dead-letters = off
//    """.stripMargin)
//
//  protected def withPoodleClient(testCode: PoodleClient => Any): Unit = {
//    implicit val actorSystem = ActorSystem("PoodleClientSpec", config)
//    val poodleClient = new PoodleClient
//    try {
//      testCode(poodleClient)
//    } finally {
//      actorSystem.shutdown()
//      actorSystem.awaitTermination()
//    }
//  }
//
//  "PoodleClient" should "post a comment in a story and retrieve a Location" in withPoodleClient { poodleClient =>
//    //poodleClient.postComment(73200, "kto jest panem", "zetos")
//    1 === 1
//  }
//
//
//
//}
