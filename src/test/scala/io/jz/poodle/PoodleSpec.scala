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

  it should "serialize Chunk with Location" in {
    val chunk = Chunk("dupa".getBytes, Some(Location(1, 2, "łóźć√")))
    serializeChunk(chunk) should equal (
      """{"payload":[100,117,112,97],"parentLocation":{"storyId":1,"commentPage":2,"commentId":"łóźć√"}}""")
  }

  it should "serialize Chunk without Location" in {
    val chunk = Chunk("dupa".getBytes, None)
    serializeChunk(chunk) should equal (
      """{"payload":[100,117,112,97]}""")
  }

  it should "deserialize Chunk with Location" in {
    val Chunk(payload, location) = deserializeChunk(
      """{"payload":[100,117,112,97],"parentLocation":{"storyId":1,"commentPage":2,"commentId":"łóźć√"}}""")
    new String(payload) should equal ("dupa")
    location should equal (Some(Location(1, 2, "łóźć√")))
  }

  it should "deserialize Chunk without Location" in {
    val Chunk(payload, location) = deserializeChunk(
      """{"payload":[100,117,112,97]}""")
    new String(payload) should equal ("dupa")
    location shouldBe empty
  }

  it should "encrypt string with SHA-1" in {
    encryptSha1Fun("salt")("dupa") should equal (
      Array(33, 72, -91, 59, -6, 52, -38, 103, 70, 64, -26, -19, -63, 38, -17, 99, -20, -8, -38, -21))
  }

}
