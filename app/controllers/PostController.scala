package controllers

import daos.PostDao
import models._
import play.api.{Mode, Play}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import repos.{Repo, PostResource, PostRepo}
import PostRepo._
import scalaz.Reader
import com.github.nscala_time.time.Imports._

/**
 * Author: @aguestuser
 * Date: 3/21/15
 */

object PostController extends PostController(PostDao)

class PostController(repo: PostRepo) extends Controller {

  def run[A](reader: Reader[PostRepo,A]): A = reader(repo)
  def runJson[A:Writes](reader: Reader[PostRepo,A]): JsValue = { Json.toJson(reader(repo)) }

  //parsers
  val postForm = Form {
    mapping(
      "Title" → text(minLength = 2),
      "Body" → text(minLength = 2)
    )(Post.apply)(Post.unapply) }

  //show
  def show(id: Long) = Action { Ok(views.html.posts.show("Silly Blog", id)) }
  def getOne(id: Long) = Action { Ok(runJson(Post.find(id))) }

  //list
  def list = Action { Ok(views.html.posts.list("Silly Blog")) }
  def getAll = Action { Ok(runJson(Post.findAll)) }

  //create
  def getCreate = Action { Ok(views.html.posts.getCreate("Write Post", postForm)) }

  def create = Action(parse.urlFormEncoded) { implicit req ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getCreate("Write Post", formWithErrors))},
      p ⇒ {
        run(Post.create(p))
        Redirect(routes.PostController.list()).flashing("success" → "Post created") } ) }

  def createJson = Action(parse.json) { implicit req ⇒
    req.body.validate[Post].fold(
      errors ⇒ {
        BadRequest(Json.obj("status" → "KO", "message" -> JsError.toFlatJson(errors))) },
      p ⇒ {
        run(Post.create(p))
        Ok(Json.obj("status" → "Ok", "message" → s"Post created")) }) }

  //edit
  def getEdit(id: Long) = Action {
    run(Post.find(id)) match {
      case None ⇒ NotFound.flashing("error" → s"Couldn't find post with id $id")
      case Some(pr) ⇒ Ok(views.html.posts.getEdit("Edit Post", id, postForm.fill(pr.post))) } }


  def edit(id: Long) = Action(parse.urlFormEncoded){ implicit req ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getEdit("Edit Post", id,formWithErrors)).flashing("error" → "Try again.") },
      p ⇒ {
        run(Post.edit(id,p))
        Redirect(routes.PostController.list()).flashing("success" → "Post edited")}) }

  def editJson(id: Long) = Action(parse.json){ implicit req ⇒
    req.body.validate[Post].fold(
      errors ⇒ {
        BadRequest(Json.obj("status" → "KO", "message" -> JsError.toFlatJson(errors))) },
      p ⇒ {
        run(Post.edit(id,p))
        Ok(Json.obj("status" → "Ok", "message" → "Post edited")) } ) }

  //delete
  def delete(id: Long) = Action { implicit req ⇒
    run(Post.delete(id)) match {
      case None ⇒ Redirect(routes.PostController.list()).flashing(
        "error" → "You tried to delete a post that doesn't exist")
      case Some(_) ⇒ Redirect(routes.PostController.list()).flashing(
        "success" → "Post deleted") } }


}
