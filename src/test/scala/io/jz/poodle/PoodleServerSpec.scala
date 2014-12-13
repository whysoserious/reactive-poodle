//package io.jz.poodle
//
//import org.scalatest.{Matchers, FlatSpec}
//
//class PoodleServerSpec extends FlatSpec with Matchers with Directives with ScalatestRouteTest {
//
  //import PoodleServer.indexRoute
//
//  def actorRefFactory = system
//
//  "PoodleServer" should "display main page" in {
//    Get() ~> indexRoute ~> check {
//      status should equal(OK)
//    }
//  }
//
//}
//Content-Type: multipart/form-data
//Content-Disposition: form-data; name="userfile"; filename="IMAG0724.jpg"
//Content-Type: image/jpeg