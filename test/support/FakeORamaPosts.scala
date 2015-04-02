package support

import models.{PostRepo, Post}

/**
 * Author: @aguestuser
 * Date: 3/30/15
 */


trait FakeORamaPosts {

  val posts = List(
    Post("first post", "what should i write?"),
    Post("second post", "i'm getting the hang of this"),
    Post("last post", "i think i'll try twitter"))

  val postRepos = (1 to 3) map { n ⇒ PostRepo(n,posts(n-1)) } toList
}
