package io.jz.poodle

import org.scalatest.{Matchers, FlatSpec}
import spray.http.HttpResponse
import spray.routing.{RequestContext, Directives, HttpService}
import spray.testkit.ScalatestRouteTest
import spray.http.StatusCodes._

class PoodleServerSpec extends FlatSpec with Matchers with Directives with ScalatestRouteTest {

  import PoodleServer.indexRoute

  def actorRefFactory = system

  "PoodleServer" should "display main page" in {
    Get() ~> indexRoute ~> check {
      status should equal(OK)
    }

  }

}
