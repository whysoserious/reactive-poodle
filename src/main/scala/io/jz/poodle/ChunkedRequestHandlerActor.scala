package io.jz.poodle

import akka.actor.{ActorLogging, Actor}
import spray.routing.{RequestContext, HttpService}

class ChunkedRequestHandlerActor(ctx: RequestContext) extends Actor with ActorLogging {

  override def receive = {
    case x => println("X >>> "+ x)
  }

}
