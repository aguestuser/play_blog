package controllers

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import scalaz.Reader

/**
 * Author: @aguestuser
 * Date: 3/21/15
 */

object PostController extends PostController(PostDao)

class PostController(repo: PostRepo) extends Controller {

  def run[A](reader: Reader[PostRepo,A]): A = reader(repo)

  //parsers
  val postForm = Form {
    mapping(
      "title" → text(minLength = 2),
      "body" → text(minLength = 2)
    )(Post.apply)(Post.unapply) }

  //show
  def show(id: Long) = Action { Ok(views.html.posts.show("Post", id)) }
  def getOne(id: Long) = Action {
    Post.find(id)(repo) match {
      case None ⇒ Ok(Json.obj())
      case Some(pr) ⇒ Ok(Json.toJson(pr)) } }

  //list
  def list = Action { Ok(views.html.posts.list("Posts")) }
  def getAll = Action { Ok(Json.toJson(Post.findAll(repo))) }

  //create
  def getCreate = Action { Ok(views.html.posts.getCreate(postForm)) }

  def create = Action(parse.urlFormEncoded) { implicit req ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getCreate(formWithErrors))},
      p ⇒ {
        Post.create(p)(repo)
        Redirect(routes.PostController.list()).flashing("success" → "Post created") } ) }

  def createJson = Action(parse.json) { implicit req ⇒
    req.body.validate[Post].fold(
      errors ⇒ {
        BadRequest(Json.obj("status" → "KO", "message" -> JsError.toFlatJson(errors))) },
      p ⇒ {
        val id = Post.create(p)(repo).get
        Ok(Json.obj("status" → "Ok", "message" → s"Post created")) }) }

  //edit
  def getEdit(id: Long) = Action {
    Post.find(id)(repo) match {
      case None ⇒ NotFound.flashing("error" → s"Couldn't find post with id $id")
      case Some(pr) ⇒ Ok(views.html.posts.getEdit(id,postForm.fill(pr.post))) } }


  def edit(id: Long) = Action(parse.urlFormEncoded){ implicit req ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getEdit(id,formWithErrors)).flashing("error" → "Try again.") },
      p ⇒ {
        Post.edit(id,p)(repo)
        Redirect(routes.PostController.list()).flashing("success" → "Post edited")}) }

  def editJson(id: Long) = Action(parse.json){ implicit req ⇒
    req.body.validate[Post].fold(
      errors ⇒ {
        BadRequest(Json.obj("status" → "KO", "message" -> JsError.toFlatJson(errors))) },
      p ⇒ {
        Post.edit(id,p)(repo)
        Ok(Json.obj("status" → "Ok", "message" → "Post edited")) } ) }

  //delete
  def delete(id: Long) = Action { implicit req ⇒
    Post.delete(id)(repo) match {
      case None ⇒ Redirect(routes.PostController.list()).flashing(
        "error" → "You tried to delete a post that doesn't exist")
      case Some(_) ⇒ Redirect(routes.PostController.list()).flashing(
        "success" → "Post deleted") } }


}
