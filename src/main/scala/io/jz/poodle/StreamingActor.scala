//package io.jz.poodle
//
//import akka.actor._
//import akka.util.ByteString
//
//object StreamingActor {
//
//  // helper methods
//
//  def propsFromString(iterable: Iterable[String], ctx: RequestContext): Props = {
//    propsFromHttpData(iterable.map(HttpData.apply), ctx)
//  }
//  def propsFromString(iterable: Iterable[String], ctx: RequestContext, charset: HttpCharset): Props = {
//    propsFromHttpData(iterable.map(HttpData.apply), ctx)
//  }
//  def propsFromByteArray(iterable: Iterable[Array[Byte]], ctx: RequestContext): Props = {
//    propsFromHttpData(iterable.map(HttpData.apply), ctx)
//  }
//  def propsFromByteString(iterable: Iterable[ByteString], ctx: RequestContext): Props = {
//    propsFromHttpData(iterable.map(HttpData.apply), ctx)
//  }
//  def propsFromHttpData(iterable: Iterable[HttpData], ctx: RequestContext): Props = {
//    Props(new StreamingActor(iterable, ctx))
//  }
//
//  // initial message sent by StreamingActor to itself
//  private case object FirstChunk
//
//  // confirmation that given chunk was sent to client
//  private case object ChunkAck
//
//}
//
//class StreamingActor(chunks: Iterable[HttpData], ctx: RequestContext) extends Actor with HttpService with ActorLogging {
//
//  import StreamingActor._
//
//  def actorRefFactory = context
//
//  val chunkIterator: Iterator[HttpData] = chunks.iterator
//
//  self ! FirstChunk
//
//  def receive = {
//
//    // send first chunk to client
//    case FirstChunk if chunkIterator.hasNext =>
//      val responseStart = HttpResponse(entity = HttpEntity(`text/html`, chunkIterator.next()))
//      ctx.responder ! ChunkedResponseStart(responseStart).withAck(ChunkAck)
//
//    // data stream is empty. Respond with Content-Length: 0 and stop
//    case FirstChunk =>
//      ctx.responder ! HttpResponse(entity = Empty)
//      context.stop(self)
//
//    // send next chunk to client
//    case ChunkAck if chunkIterator.hasNext =>
//      val nextChunk = MessageChunk(chunkIterator.next())
//      ctx.responder ! nextChunk.withAck(ChunkAck)
//
//    // all chunks were sent. stop.
//    case ChunkAck =>
//      ctx.responder ! ChunkedMessageEnd
//      context.stop(self)
//
//    //
//    case x => unhandled(x) //stop immediately
//  }
//
//}
