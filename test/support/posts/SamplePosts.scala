package support.posts

import models.Post
import repo.PostResource

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */

trait SamplePosts {

  lazy val post = Post("first post", "what should i write?")

  lazy val posts = List(
    Post("first post", "what should i write?"),
    Post("second post", "i'm getting the hang of this"),
    Post("last post", "i think i'll try twitter"))

  lazy val postResources = List(
    PostResource(1,Post("first post", "what should i write?")),
    PostResource(2,Post("second post", "i'm getting the hang of this")),
    PostResource(3,Post("last post", "i think i'll try twitter")))
}
