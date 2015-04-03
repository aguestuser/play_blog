package controllers

import controllers.PostController.edit
import models.{Post, PostRepo, PostResource}
import org.specs2.mock._
import org.specs2.mutable._
import play.api.libs.json.{JsArray, JsNumber, Json}
import play.api.test.Helpers._
import play.api.test._
import support.posts.{PostControllerExpectedValues, SamplePosts}


/**
 * Author: @aguestuser
 * Date: 3/26/15
 */

class PostController$Test extends Specification with SamplePosts with PostControllerExpectedValues with Mockito {

  val fakeRepo = mock[PostRepo]

  fakeRepo.find(0) returns None
  fakeRepo.find(1) returns Some(PostResource(1,post))
  fakeRepo.findAll returns { (1 to 3).toList map { n ⇒ PostResource(n,posts(n - 1)) } }

  fakeRepo.create(post) returns Some(1L)
  fakeRepo.create(Post("title", "b")) returns None
  fakeRepo.create(Post("a", "body")) returns None
  fakeRepo.create(Post("a", "b")) returns None

  fakeRepo.edit(1, Post("new title", "new body")) returns Some(1)
  fakeRepo.edit(1, Post("title","b")) returns None
  fakeRepo.edit(1, Post("a","body")) returns None
  fakeRepo.edit(1, Post("a","b")) returns None

  fakeRepo.delete(1) returns Some(1)
  fakeRepo.delete(0) returns None

  object fakeController extends PostController(fakeRepo)

  "Post controller" should {

    "render a post display page" >> {

      val res = fakeController.show(1)(FakeRequest())

      status(res) === OK
      contentType(res) must beSome.which(_ == "text/html")
      contentAsString(res) must contain("Post")
    }

    "retrieve a post" >> {

      val res = fakeController.getOne(1)(FakeRequest())

      status(res) === OK
      contentType(res) must beSome.which(_ == "application/json")
      contentAsJson(res) === Json.toJson(PostResource(1,post))
    }

    "not retrieve a non-existent post" >> {

      val res = fakeController.getOne(0)(FakeRequest())

      status(res) === OK
      contentType(res) must beSome.which(_ == "application/json")
      contentAsJson(res) === Json.obj()
    }

    "render a page that shows all posts" >> {

      val res = fakeController.list(FakeRequest())

      status(res) === OK
      contentType(res) must beSome.which(_ == "text/html")
      contentAsString(res) must contain("Posts")
    }

    "retrieve all posts" >> {

      val res = fakeController.getAll(FakeRequest())

      status(res) === OK
      contentType(res) must beSome.which(_ == "application/json")
      contentAsJson(res) === Json.toJson(postResources)
    }

    "render a page to create a post" >> {

      val res = fakeController.getCreate(FakeRequest())

      status(res) === OK
      contentType(res) must beSome.which(_ == "text/html")
      contentAsString(res) === getCreateHtml
    }

    "create a post from a form" >> {

      val res = fakeController.create(FakeRequest().withBody(Map(
          "title" → Seq(s"${post.title}"),
          "body" → Seq(s"${post.body}") ) ) )

      status(res) === SEE_OTHER
      headers(res) === Map(
        "Location" → "/posts",
        "Set-Cookie" → "PLAY_FLASH=\"success=Post+created\"; Path=/; HTTPOnly")

    }

    "not create a post from a form with errors" >> {

      "title less than 2 char" >> {

        val res = fakeController.create(FakeRequest().withBody(Map(
          "title" → Seq("should work"),
          "body" → Seq("a"))))

        status(res) === BAD_REQUEST
        headers(res) === Map("Content-Type" -> "text/html; charset=utf-8")
        contentAsString(res) must contain("<form action=\"/posts/create\" method=\"POST\" >")
      }

      "body less than 2 char" >> {

        val res = fakeController.create(FakeRequest().withBody(Map(
          "title" → Seq("a"),
          "body" → Seq("should work"))))

        status(res) === BAD_REQUEST
        headers(res) === Map("Content-Type" -> "text/html; charset=utf-8")
        contentAsString(res) must contain("<form action=\"/posts/create\" method=\"POST\" >")

      }

      "title and body less than 2 char" >> {

        lazy val res = fakeController.create(FakeRequest().withBody(Map(
          "title" → Seq("a"),
          "body" → Seq("b"))))

        status(res) === BAD_REQUEST
        headers(res) === Map("Content-Type" -> "text/html; charset=utf-8")
        contentAsString(res) must contain("<form action=\"/posts/create\" method=\"POST\" >")
      }
    }

    "create a post from a json request" >> {

      val res = fakeController.createJson(FakeRequest().withBody(Json.toJson(post)))

      status(res) === OK
      headers(res) === Map("Content-Type" -> "application/json; charset=utf-8")
      contentAsJson(res) === Json.obj("status" → "Ok", "message" → "Post created")
    }

    "not create a post from json with errors" >> {

      "title less than 2 char" >> {

        val res = fakeController.createJson(FakeRequest().withBody(Json.obj(
          "title" → "title",
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

      "body less than 2 char" >> {

        val res = fakeController.createJson(FakeRequest().withBody(Json.obj(
          "title" → "a",
          "body" → "body")))

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

      "title and body less than 2 char" >> {

        lazy val res = fakeController.createJson(FakeRequest().withBody(Json.obj(
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

    "render a page to edit a post" >> {

      val res = fakeController.getEdit(1)(FakeRequest())

      status(res) === OK
      headers(res) === Map("Content-Type" -> "text/html; charset=utf-8")
      contentAsString(res) === getEditHtml(1)
    }

    "not render a page to edit a post that doesn't exist" >> {

      val res = fakeController.getEdit(0)(FakeRequest())

      status(res) === NOT_FOUND
      headers(res) === Map("Set-Cookie" → "PLAY_FLASH=\"error=Couldn%27t+find+post+with+id+0\"; Path=/; HTTPOnly")
    }

    "edit a post from a form" >> {

      val res = fakeController.edit(1)(FakeRequest().withBody(Map(
        "title" → Seq(s"${post.title}"),
        "body" → Seq(s"${post.body}"))))

      status(res) === SEE_OTHER
      headers(res) === Map(
        "Location" → "/posts",
        "Set-Cookie" → "PLAY_FLASH=\"success=Post+edited\"; Path=/; HTTPOnly")
    }

    "not save improperly formatted edits" >> {

      val badTitle = fakeController.edit(1)(FakeRequest().withBody(Map(
        "title" → Seq("a"),
        "body" → Seq("okay"))))
      lazy val badBody = edit(1)(FakeRequest().withBody(Map(
        "title" → Seq("okay"),
        "body" → Seq("b"))))
      lazy val badBoth = edit(1)(FakeRequest().withBody(Map(
        "title" → Seq("a"),
        "body" → Seq("b"))))

      status(badTitle) === BAD_REQUEST
      status(badBody) === BAD_REQUEST
      status(badBoth) === BAD_REQUEST
    }

    "edit a post from json" >> {

      val res = fakeController.editJson(1)(FakeRequest().withBody(Json.toJson(post)))

      status(res) === OK
      headers(res) === Map("Content-Type" -> "application/json; charset=utf-8")
      contentAsJson(res) === Json.obj("status" → "Ok", "message" → "Post edited")
    }

    "delete a post" >> {

      val res = fakeController.delete(1)(FakeRequest())

      status(res) === SEE_OTHER
      headers(res) === Map(
        "Location" → "/posts",
        "Set-Cookie" → "PLAY_FLASH=\"success=Post+deleted\"; Path=/; HTTPOnly")
    }

    "not delete a post that doesn't exist" >> {

      val res = fakeController.delete(0)(FakeRequest())

      status(res) === SEE_OTHER
      headers(res) === Map(
        "Location" → "/posts",
        "Set-Cookie" → "PLAY_FLASH=\"error=You+tried+to+delete+a+post+that+doesn%27t+exist\"; Path=/; HTTPOnly")
    }
  }
}
