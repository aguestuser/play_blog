package controllers

import PostController.getOne
import models.PostRepo
import org.specs2.execute.AsResult
import org.specs2.mutable.Specification
import org.specs2.specification.After
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import support.posts.WithFakePosts


/**
 * Author: @aguestuser
 * Date: 3/26/15
 */

class PostController$Test extends Specification {

  "Post controller" should {

    "render a post display page" >> new WithFakePosts {

      case class ex1() extends After {
        val ids = setup(List(posts.head))
        def after = teardown(ids)
        def run = this {

//          val res = route(FakeRequest(GET, s"/posts/${ids.head}")).get
          val res = PostController.show(ids.head)(FakeRequest())

          status(res) === OK
          contentType(res) must beSome.which(_ == "text/html")
          contentAsString(res) must contain("Post") } }

      AsResult.effectively(ex1().run)
    }

    "retrieve a post" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(List(posts.head))
        def after = teardown(ids)
        def run = this {

//          val res = route(FakeRequest(GET, s"/posts/get/${ids.head}")).get
          val res = getOne(ids.head)(FakeRequest())

          status(res) === OK
          contentType(res) must beSome.which(_ == "application/json")
          contentAsJson(res) === Json.toJson(PostRepo(ids.head,posts.head)) } }

      AsResult.effectively(ex().run)
    }

    "not retrieve a non-existent post" >> new WithFakePosts {

      val res = route(FakeRequest(GET, s"/posts/get/0")).get

      status(res) === OK
      contentType(res) must beSome.which(_ == "application/json")
      contentAsJson(res) === Json.obj()
    }

    "render a page that shows all posts" >> new WithFakePosts {

      val res = route(FakeRequest(GET, "/posts/")).get

      status(res) === OK
      contentType(res) must beSome.which(_ == "text/html")
      contentAsString(res) must contain("Posts")
    }

    "retrieve all posts" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        val expectedPostRepos = { ids zip posts } map { case(i,p) ⇒ PostRepo(i,p) }
        def after = teardown(ids)
        def run = this {

          lazy val res = route(FakeRequest(GET, "/posts/get/")).get

          status(res) === OK
          contentType(res) must beSome.which(_ == "application/json")
          contentAsJson(res) === Json.toJson(expectedPostRepos) } }

      AsResult.effectively(ex().run)
    }

    "render a page to create a post" >> new WithFakePosts {

      val res = route(FakeRequest(GET, "/posts/create/")).get

      status(res) === OK
      contentType(res) must beSome.which(_ == "text/html")
      contentAsString(res) === createPostHtml

    }

    "create a post from a form" >> new WithFakePosts {

      case class ex() extends After {
        def after = teardown(last)
        def run = this {

          val res = route(FakeRequest(POST, "/posts/create/")
            .withFormUrlEncodedBody(
              "title" → s"${posts.head.title}",
              "body" → s"${posts.head.body}"))
            .get

          status(res) === SEE_OTHER
          headers(res) === Map(
            "Location" → "/posts/",
            "Set-Cookie" → "PLAY_FLASH=\"success=Post+created\"; Path=/; HTTPOnly") } }

      AsResult.effectively(ex().run)
    }

    "create a post from a json request" >> new WithFakePosts {

      case class ex() extends After {
        def after = teardown(last)
        def run = this {

          lazy val res = route(FakeRequest(POST, "/posts/create/").withJsonBody(
            Json.toJson(posts.head))).get

          status(res) === OK
          headers(res) === Map("Content-Type" -> "application/json; charset=utf-8")
          contentAsJson(res) === Json.obj("status" → "Ok", "message" → "Post created") } }

      AsResult.effectively(ex().run)
    }

//    "render a page to edit a post" >> new WithFakePosts {
//
//      val res = route(FakeRequest(GET, s"/posts/edit/${posts.head.id}"))
//    }
  }
}
