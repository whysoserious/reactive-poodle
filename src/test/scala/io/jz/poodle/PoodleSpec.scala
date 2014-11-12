package io.jz.poodle

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random


class PoodleSpec extends FlatSpec with Matchers {

  import io.jz.poodle.Poodle._

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

  it should "encrypt string with AES and with given secret" in {
    encryptFun("AES", "0123456789012345")("dupa".getBytes) should equal(
      Array(111, -2, -31, -119, -116, -113, 74, 101, 95, -20, 87, -83, -48, 26, 82, 105))
  }

  it should "decrypt string with AES and with given secret" in {
    val actual: Array[Byte] = decryptFun("AES", "0123456789012345")(
      Array[Byte](111, -2, -31, -119, -116, -113, 74, 101, 95, -20, 87, -83, -48, 26, 82, 105))
    val expected = "dupa".getBytes
    actual should equal(expected)
  }

  it should "encode byte array to base64" in {
    encodeBase64(Array[Byte](0, 2, 4, 101)) should equal ("AAIEZQ==")
  }

  it should "decode base64 string to byte array" in {
    decodeBase64("AAIEZQ==") should equal (Array[Byte](0, 2, 4, 101))
  }

  it should "encrypt, encode, decode, decrypt a byte array" in {
    val actual = new String(
      decryptFun("AES", "0123456789012345")(
        decodeBase64(
          encodeBase64(
            encryptFun("AES", "0123456789012345")("dupa".getBytes)))))
    actual should equal("dupa")
  }

  it should "return random UserAgent" in {
    val randomUserAgent = randomUserAgentFun(random = new Random(2))
    randomUserAgent() should equal ("""Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36""")
    randomUserAgent() should equal ("""Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36""")
    randomUserAgent() should equal ("""Mozilla/5.0 (iPhone; CPU iPhone OS 8_1 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12B411 Safari/600.1.4""")
  }

}
