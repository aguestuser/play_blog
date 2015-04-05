package support.posts

import models.Post
import repos.PostResource
import util.Environment


/**
 * Author: @aguestuser
 * Date: 4/3/15
 */

trait SamplePosts {

  import Environment.s17

  lazy val post = Post("first post", "what should i write?")

  lazy val posts = List(
    Post("first post", "what should i write?"),
    Post("second post", "i'm getting the hang of this"),
    Post("last post", "i think i'll try twitter"))

  lazy val postResources = List(
    PostResource(1,s17,s17,Post("first post", "what should i write?")),
    PostResource(2,s17,s17,Post("second post", "i'm getting the hang of this")),
    PostResource(3,s17,s17,Post("last post", "i think i'll try twitter")))
}
