package io.jz.poodle

import java.security.MessageDigest

import org.json4s.{Formats, NoTypeHints}
import org.json4s.jackson.Serialization

import scala.util.Random
import org.json4s.jackson.Serialization.{read, write}

object Poodle {

  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  case class Location(storyId: Int, commentPage: Int, commentId: String)

  case class Chunk(payload: Array[Byte], parentLocation: Option[Location])

  // receive data as stream

  // slice stream to fixed size chunks

  // get random story id
  def randomStoryIdFun(from: Int, to: Int, random: Random = new Random): () => Int = {
    () => random.nextInt(to - from) + from
  }

  //serialize chunk
  def serializeChunk(chunk: Chunk)(implicit formats: Formats): String = {
    write(chunk)
  }

  def deserializeChunk(str: String)(implicit formats: Formats): Chunk = {
    read[Chunk](str)
  }

  // remember cookie ?

  // create initial chunk

  // create chunk with location

  // encode chunk with SHA-1
  def encrypt(salt: String): String => Array[Byte] = {
    input =>
      val md: MessageDigest = MessageDigest.getInstance("SHA")
      md.digest((salt + input).getBytes)
  }

  // encode chunk with Base 64

  // POST chunk as comment

  // memoize story_id, comment_page, comment_id

  // POST chunk as comment with parent_story_id, parent_comment_page, parent_comment_id





}
