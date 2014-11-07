package io.jz.poodle

import io.jz.poodle.poodle.Poodle
import org.scalatest.{Matchers, FlatSpec}

import scala.util.Random


class PoodleSpec extends FlatSpec with Matchers {

  import Poodle._

  "Poodle" should "generate random storyId" in {
    val randomStoryId = randomStoryIdFun(1, 10, new Random(1))
    randomStoryId() should equal (7)
    randomStoryId() should equal (2)
    randomStoryId() should equal (2)
  }

  it should "asdfasdf" in {
    assert(true)
  }


}
