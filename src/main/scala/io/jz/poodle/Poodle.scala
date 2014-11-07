package io.jz.poodle.poodle

import scala.util.Random

object Poodle {

  // receive data as stream

  // split stream to fixed size chunks

  // get random story id
  def randomStoryIdFun(from: Int, to: Int)(random: Random = new Random): () => Int = {
    () => random.nextInt(to - from) + from
  }

  // remember cookie ?

  // encode chunk with SHA-1

  // encode chunk with Base 64

  // POST chunk as comment

  // memoize story_id, comment_page, comment_id

  // POST chunk as comment with parent_story_id, parent_comment_page, parent_comment_id





}
