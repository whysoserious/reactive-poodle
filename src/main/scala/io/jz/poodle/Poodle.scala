package io.jz.poodle

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import akka.http.model.MediaType
import akka.http.model.MediaTypes._
import akka.parboiled2.util.Base64
import org.json4s.jackson.Serialization
import org.json4s.{Formats, NoTypeHints}

import scala.util.Random

object Poodle {

  implicit val formats: Formats = Serialization.formats(NoTypeHints) + ByteStringSerializer

  case class ChunkLocation(storyId: Int, commentPage: Int, commentId: String, path: String)

  def randomStoryIdFun(from: Int, to: Int, random: Random = new Random): () => Int = {
    () => random.nextInt(to - from) + from
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

  def mimeType: String => MediaType = {
    lazy val ExtensionRegex = """^.*\.(.*)$""".r
    lazy val defaultMimeType = `application/octet-stream`
    filename: String => {
      filename match {
        case ExtensionRegex(extension) => forExtension(extension).getOrElse(defaultMimeType)
        case _ => defaultMimeType
      }
    }
  }

}
