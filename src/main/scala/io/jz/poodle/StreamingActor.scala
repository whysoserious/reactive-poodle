package io.jz.poodle

import akka.actor._
import akka.util.ByteString
import spray.http.HttpEntity.Empty
import spray.http.MediaTypes._
import spray.http._
import spray.routing.{HttpService, RequestContext}

object StreamingActor {

  // helper methods
  
  def propsFromString(iterable: Iterable[String], ctx: RequestContext): Props = {
    propsFromHttpData(iterable.map(HttpData.apply), ctx)
  }
  def propsFromString(iterable: Iterable[String], ctx: RequestContext, charset: HttpCharset): Props = {
    propsFromHttpData(iterable.map(HttpData.apply), ctx)
  }
  def propsFromByteArray(iterable: Iterable[Array[Byte]], ctx: RequestContext): Props = {
    propsFromHttpData(iterable.map(HttpData.apply), ctx)
  }
  def propsFromByteString(iterable: Iterable[ByteString], ctx: RequestContext): Props = {
    propsFromHttpData(iterable.map(HttpData.apply), ctx)
  }
  def propsFromHttpData(iterable: Iterable[HttpData], ctx: RequestContext): Props = {
    Props(new StreamingActor(iterable, ctx))
  }
  
  // initial message sent by StreamingActor to itself
  private case object FirstChunk
  
  // confirmation that given chunk was sent to client
  private case object ChunkAck

}

class StreamingActor(chunks: Iterable[HttpData], ctx: RequestContext) extends Actor with HttpService with ActorLogging {

  import io.jz.poodle.StreamingActor._

  def actorRefFactory = context

  val chunkIterator: Iterator[HttpData] = chunks.iterator

  self ! FirstChunk

  def receive = {

    // send first chunk to client
    case FirstChunk if chunkIterator.hasNext =>
      val responseStart = HttpResponse(entity = HttpEntity(`text/html`, chunkIterator.next()))
      ctx.responder ! ChunkedResponseStart(responseStart).withAck(ChunkAck)

    // data stream is empty. Respond with Content-Length: 0 and stop
    case FirstChunk =>
      ctx.responder ! HttpResponse(entity = Empty)
      context.stop(self)

    // send next chunk to client
    case ChunkAck if chunkIterator.hasNext =>
      val nextChunk = MessageChunk(chunkIterator.next())
      ctx.responder ! nextChunk.withAck(ChunkAck)

    // all chunks were sent. stop.
    case ChunkAck =>
      ctx.responder ! ChunkedMessageEnd
      context.stop(self)

    //
    case x => unhandled(x)
  }

}

//object ChunkedSpray extends App with SimpleRoutingApp {
//
//  import io.jz.poodle.StreamingActor._
//
//  implicit val actorSystem = ActorSystem()
//
//  lazy val stream: Stream[String] = "first" #:: "second" #:: "third" #:: Stream.empty
//
//  lazy val emptyStream: Stream[Array[Byte]] = Stream.empty
//
//  startServer(interface = "localhost", port = 8080) {
//    get {
//      path("stream") {
//        ctx =>
//          actorRefFactory.actorOf(fromString(stream, ctx))
//      } ~
//      path("empty-stream") {
//        ctx =>
//          actorRefFactory.actorOf(fromByteArray(emptyStream, ctx))
//      }
//    }
//  }
//}
//
//~
//path("hello") {
//complete {
//<html>
//<form action="/upload" method="post" enctype="multipart/form-data">
//<input type="file" name="userfile" />
//<input type="text" name="imgdec" />
//<button name="upload" type="submit" value="Submit" />
//</form>
//</html>
//}
//}
//} ~
//post {
//path("upload") {
//ctx =>
//ctx.complete("OK")
//}
