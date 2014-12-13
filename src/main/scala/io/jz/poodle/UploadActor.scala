package io.jz.poodle

import akka.actor.Actor.Receive
import akka.actor.{ ActorLogging, Actor }
import akka.util.ByteString
import io.jz.poodle Poodle.ChunkLocation
import spray.http.HttpData
import spray.http.HttpHeaders.`Content-Type`
import scala.concurrent.duration._

object UploadActor {

}

class UploadActor(
  httpData: HttpData,
  contentType: Option[`Content-Type`],
  chunkSize: Int
) extends Actor with ActorLogging {

  private var previousChunkLocation: Option[ChunkLocation] = None
  private var chunks: Stream[ByteString] = httpData.toByteString.sliding(chunkSize).toStream

  //TODO empty?
  self ! chunks.head

  override def receive: Receive = {
    case chunk: ByteString => handleChunk(chunk)
    case x => unhandled(x)
  }

  def handleChunk(chunk: ByteString): Unit = {

  }

}
