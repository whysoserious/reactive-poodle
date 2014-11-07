package io.jz.poodle

import org.scalatest.{Matchers, FlatSpec}

import scala.util.Random


class PoodleSpec extends FlatSpec with Matchers {

  import Poodle._

  "Poodle" should "generate random storyId" in {
    val randomStoryId = randomStoryIdFun(1, 10, new Random(1))
    randomStoryId() should equal (7)
    randomStoryId() should equal (2)
    randomStoryId() should equal (2)
  }

  it should "serialize Chunk" in {
    val chunk = Chunk("dupa".getBytes, Some(Location(1, 2, "łóźć√")))
    serializeChunk(chunk) should equal (
      """{"payload":[100,117,112,97],"parentLocation":{"storyId":1,"commentPage":2,"commentId":"łóźć√"}}""")
  }

  it should "deserialize Chunk" in {
    val Chunk(payload, location) = deserializeChunk(
      """{"payload":[100,117,112,97],"parentLocation":{"storyId":1,"commentPage":2,"commentId":"łóźć√"}}""")
    new String(payload) should equal ("dupa")
    location should equal (Some(Location(1, 2, "łóźć√")))
  }


}
