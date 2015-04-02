package controllers

import controllers.PostController.{create, createJson, edit, editJson, getAll, getCreate, getEdit, getOne, list, delete}
import models.PostRepo
import org.specs2.execute.AsResult
import org.specs2.mutable.Specification
import org.specs2.specification.After
import play.api.libs.json.{JsNumber, JsArray, Json}
import play.api.test.Helpers._
import play.api.test._
import support.posts.{PostControllerExpectedValues, WithFakePosts}


/**
 * Author: @aguestuser
 * Date: 3/26/15
 */

class PostController$Test extends Specification with PostControllerExpectedValues {

  "Post controller" should {

    "render a post display page" >> new WithFakePosts {

      case class ex1() extends After {
        val id = setup(List(post)).head
        def after = teardown(List(id))
        def run = this {

          val res = PostController.show(id)(FakeRequest())

          status(res) === OK
          contentType(res) must beSome.which(_ == "text/html")
          contentAsString(res) must contain("Post") } }

      AsResult.effectively(ex1().run)
    }

    "retrieve a post" >> new WithFakePosts {

      case class ex() extends After {
        val id = setup(List(post)).head
        def after = teardown(List(id))
        def run = this {

          val res = getOne(id)(FakeRequest())

          status(res) === OK
          contentType(res) must beSome.which(_ == "application/json")
          contentAsJson(res) === Json.toJson(PostRepo(id,post)) } }

      AsResult.effectively(ex().run)
    }

    "not retrieve a non-existent post" >> new WithFakePosts {

      val res = getOne(0)(FakeRequest())

      status(res) === OK
      contentType(res) must beSome.which(_ == "application/json")
      contentAsJson(res) === Json.obj()
    }

    "render a page that shows all posts" >> new WithFakePosts {

      val res = list(FakeRequest())

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

          val res = getAll(FakeRequest())

          status(res) === OK
          contentType(res) must beSome.which(_ == "application/json")
          contentAsJson(res) === Json.toJson(expectedPostRepos) } }

      AsResult.effectively(ex().run)
    }

    "render a page to create a post" >> new WithFakePosts {

      val res = getCreate(FakeRequest())

      status(res) === OK
      contentType(res) must beSome.which(_ == "text/html")
      contentAsString(res) === getCreateHtml

    }

    "create a post from a form" >> new WithFakePosts {

      case class ex() extends After {
        def after = teardown(last)
        def run = this {

          val res = create(FakeRequest().withBody(Map(
              "title" → Seq(s"${post.title}"),
              "body" → Seq(s"${post.body}") ) ) )

          status(res) === SEE_OTHER
          headers(res) === Map(
            "Location" → "/posts",
            "Set-Cookie" → "PLAY_FLASH=\"success=Post+created\"; Path=/; HTTPOnly") } }

      AsResult.effectively(ex().run)
    }

    "not create a post from a form with errors" >> {

      "title less than 2 char" >> new WithFakePosts {

        val res = create(FakeRequest().withBody(Map(
          "title" → Seq("should work"),
          "body" → Seq("a"))))

        status(res) === BAD_REQUEST
        headers(res) === Map("Content-Type" -> "text/html; charset=utf-8")
        contentAsString(res) === createErrorHtml
      }

      "body less than 2 char" >> new WithFakePosts {

        val res = create(FakeRequest().withBody(Map(
          "title" → Seq("a"),
          "body" → Seq("should work"))))

        status(res) === BAD_REQUEST

      }

      "title and body less than 2 char" >> new WithFakePosts {

        lazy val res = create(FakeRequest().withBody(Map(
          "title" → Seq("a"),
          "body" → Seq("b"))))

        status(res) === BAD_REQUEST
      }
    }

    "create a post from a json request" >> new WithFakePosts {

      case class ex() extends After {
        def after = teardown(last)
        def run = this {

          val res = createJson(FakeRequest().withBody(Json.toJson(post)))

          status(res) === OK
          headers(res) === Map("Content-Type" -> "application/json; charset=utf-8")
          contentAsJson(res) === Json.obj("status" → "Ok", "message" → "Post created") } }

      AsResult.effectively(ex().run)

    }

    "not create a post from json with errors" >> {

      "title less than 2 char" >> new WithFakePosts {

        val res = createJson(FakeRequest().withBody(Json.obj(
          "title" → "should work",
          "body" → "a")))

        status(res) === BAD_REQUEST
        headers(res) === Map("Content-Type" -> "application/json; charset=utf-8")
        contentAsJson(res) ===
          Json.obj(
            "status" → "KO",
            "message" →
              Json.obj(
                "obj.body" →
                  JsArray(Seq(
                    Json.obj(
                      "msg" → "error.minLength",
                      "args" → JsArray(Seq(JsNumber(2))))))))
      }

      "body less than 2 char" >> new WithFakePosts {

        val res = createJson(FakeRequest().withBody(Json.obj(
          "title" → "a",
          "body" → "should work")))

        status(res) === BAD_REQUEST
        contentAsJson(res) ===
          Json.obj(
            "status" → "KO",
            "message" →
              Json.obj(
                "obj.title" →
                  JsArray(Seq(
                    Json.obj(
                      "msg" → "error.minLength",
                      "args" → JsArray(Seq(JsNumber(2))))))))

      }

      "title and body less than 2 char" >> new WithFakePosts {

        lazy val res = createJson(FakeRequest().withBody(Json.obj(
          "title" → "a",
          "body" → "b")))

        status(res) === BAD_REQUEST
        contentAsJson(res) ===
          Json.obj(
            "status" → "KO",
            "message" →
              Json.obj(
                "obj.body" →
                  JsArray(Seq(
                    Json.obj(
                      "msg" → "error.minLength",
                      "args" → JsArray(Seq(JsNumber(2)))))),
                "obj.title" →
                  JsArray(Seq(
                    Json.obj(
                      "msg" → "error.minLength",
                      "args" → JsArray(Seq(JsNumber(2))))))))

      }
    }

    "render a page to edit a post" >> new WithFakePosts {

      case class ex() extends After {
        val id = setup(List(post)).head
        def after = teardown(List(id))
        def run = this {

          val res = getEdit(id)(FakeRequest())

          status(res) === OK
          headers(res) === Map("Content-Type" -> "text/html; charset=utf-8")
          contentAsString(res) === getEditHtml(id)

        }}

      AsResult.effectively(ex().run)
    }

    "not render a page to edit a post that doesn't exist" >> new WithFakePosts {

      val res = getEdit(0)(FakeRequest())

      status(res) === NOT_FOUND
      headers(res) === Map("Set-Cookie" → "PLAY_FLASH=\"error=Couldn%27t+find+post+with+id+0\"; Path=/; HTTPOnly")

    }

    "edit a post from a form" >> new WithFakePosts {

      case class ex() extends After {
        val id = setup(List(post)).head
        def after = teardown(List(id))
        def run = this {

          val res = edit(id)(FakeRequest().withBody(Map(
            "title" → Seq(s"${post.title}"),
            "body" → Seq(s"${post.body}"))))

          status(res) === SEE_OTHER
          headers(res) === Map(
            "Location" → "/posts",
            "Set-Cookie" → "PLAY_FLASH=\"success=Post+edited\"; Path=/; HTTPOnly") } }

      AsResult.effectively(ex().run)
    }

    "not save improperly formatted edits" >> new WithFakePosts {

      case class ex() extends After {
        val id = setup(List(post)).head
        def after = teardown(List(id))
        def run = this {

          val badTitle = edit(id)(FakeRequest().withBody(Map(
            "title" → Seq("a"),
            "body" → Seq("okay"))))
          lazy val badBody = edit(id)(FakeRequest().withBody(Map(
            "title" → Seq("okay"),
            "body" → Seq("b"))))
          lazy val badBoth = edit(id)(FakeRequest().withBody(Map(
            "title" → Seq("a"),
            "body" → Seq("b"))))

          status(badTitle) === BAD_REQUEST
          status(badBody) === BAD_REQUEST
          status(badBoth) === BAD_REQUEST
        }}

      AsResult.effectively(ex().run)
    }

    "edit a post from json" >> new WithFakePosts {

      case class ex() extends After {
        val id = setup(List(post)).head
        def after = teardown(List(id))
        def run = this {

          val res = editJson(id)(FakeRequest().withBody(Json.toJson(post)))

          status(res) === OK
          headers(res) === Map("Content-Type" -> "application/json; charset=utf-8")
          contentAsJson(res) === Json.obj("status" → "Ok", "message" → "Post edited") } }

      AsResult.effectively(ex().run)
    }

    "delete a post" >> new WithFakePosts {

      case class ex() extends After {
        val id = setup(List(post)).head
        def after = teardown(List(id))
        def run = this {

          val res = delete(id)(FakeRequest())

          status(res) === SEE_OTHER
          headers(res) === Map(
            "Location" → "/posts",
            "Set-Cookie" → "PLAY_FLASH=\"success=Post+deleted\"; Path=/; HTTPOnly")

        }}

      AsResult.effectively(ex().run)
    }

    "not delete a post that doesn't exist" >> new WithFakePosts {

      val res = delete(0)(FakeRequest())

      status(res) === SEE_OTHER
      headers(res) === Map(
        "Location" → "/posts",
        "Set-Cookie" → "PLAY_FLASH=\"error=You+tried+to+delete+a+post+that+doesn%27t+exist\"; Path=/; HTTPOnly"
      )

    }
  }
}
