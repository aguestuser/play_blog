package controllers

import models.PostRepoImpl
import org.specs2.mock._
import org.specs2.mutable.Specification
import play.api.mvc.{Result, Action}
import play.api.test.Helpers._
import play.api.test._
import sample_data.FakePosts

import scala.concurrent.Future

/**
 * Author: @aguestuser
 * Date: 3/26/15
 */

class PostController$Test extends Specification with Mockito with FakePosts {

  val mpr = mock[PostRepoImpl]
  mpr.find(postRepos.head.id) returns Some(postRepos.head)
  mpr.findAll returns postRepos
  mpr.create(posts.head) returns Some(postRepos.head.id)
  mpr.delete(postRepos.head.id) returns Some(1)

  object fakeController extends PostController(mpr)

  "Post controller" should {

      "make a page to show a post" in new WithApplication {

        val res: Future[Result] = fakeController.show(1)

        status(res) === OK
        contentType(res) must beSome.which(_ == "text/html")
        contentAsString(res) must contain("Post")
      }

      "retrieve a post to show" >> new WithApplication {

        lazy val res = route(FakeRequest(GET, s"posts/get/${postRepos.head.id}")).get

        status(res) === OK
        contentType(res) must beSome.which(_ == "application/json")
        contentAsString(res) must contain(postRepos.head.title)
        contentAsString(res) must contain(postRepos.head.body)
      }
    }
}
