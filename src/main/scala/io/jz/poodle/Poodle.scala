package io.jz.poodle

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read, write}
import org.json4s.{Formats, NoTypeHints}
import org.parboiled.common.Base64

import scala.util.Random

object Poodle {

  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  case class Location(storyId: Int, commentPage: Int, commentId: String)

  case class Chunk(payload: Array[Byte], parentLocation: Option[Location])

  def randomStoryIdFun(from: Int, to: Int, random: Random = new Random): () => Int = {
    () => random.nextInt(to - from) + from
  }

  def serializeChunk(chunk: Chunk)(implicit formats: Formats): String = {
    write(chunk)
  }

  def deserializeChunk(str: String)(implicit formats: Formats): Chunk = {
    read[Chunk](str)
  }

  def encryptFun(algorithmName: String, secret: String): Array[Byte] => Array[Byte] = {
    val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
    val encipher = Cipher.getInstance(algorithmName + "/ECB/PKCS5Padding")
    bytes: Array[Byte] => {
      encipher.init(Cipher.ENCRYPT_MODE, secretKey)
      encipher.doFinal(bytes)
    }
  }

  def decryptFun(algorithmName: String, secret: String): Array[Byte] => Array[Byte] = {
    val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
    val encipher = Cipher.getInstance(algorithmName + "/ECB/PKCS5Padding")
    bytes: Array[Byte] => {
      encipher.init(Cipher.DECRYPT_MODE, secretKey)
      encipher.doFinal(bytes)
    }
  }

  def encodeBase64(bytes: Array[Byte]): String = {
    Base64.rfc2045.encodeToString(bytes, false)
  }

  def decodeBase64(str: String): Array[Byte] = {
    Base64.rfc2045.decode(str)
  }

  def randomUserAgentFun(resourceName: String = "/user-agents", random: Random = new Random): () => String = {
    lazy val userAgents = scala.io.Source.fromInputStream(getClass.getResourceAsStream(resourceName)).getLines().toSeq
    () => userAgents(random.nextInt(userAgents.size))
  }

}
