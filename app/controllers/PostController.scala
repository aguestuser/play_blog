package controllers

import models.PostDao._

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}


/**
 * Author: @aguestuser
 * Date: 3/21/15
 */

object PostController extends PostController(PostDao)

class PostController(repo: PostRepoImpl) extends Controller {

  import scalaz.Reader

  val postForm = Form {
    mapping(
      "title" → text(minLength = 2),
      "body" → text(minLength = 2)
    )(Post.apply)(Post.unapply) }

  private def run[A](reader: Reader[PostRepoImpl,A]): A = { reader(repo) }

  def show(id: Long) = Action { Ok(views.html.posts.show("Post")) }
  def getOne(id: Long) = Action { Ok(Json.toJson(run(Post.find(id)))) }

  def list = Action { implicit request ⇒ Ok(views.html.posts.list("Posts")) }
  def getAll = Action { Ok(Json.toJson(run(Post.findAll))) }

  def getCreate = Action { Ok(views.html.posts.getCreate(postForm)) }
  def create = Action { implicit req ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getCreate(formWithErrors))},
      p ⇒ {
        run(Post.create(p)) match {
          case None ⇒ NotFound.flashing("error" → "Whoops!")
          case Some(id) ⇒ Redirect(routes.PostController.list()).flashing(
            "success" → "Post created!") } }) }

  def getEdit(id: Long) = Action {
    run(Post.find(id)) match {
      case None ⇒ NotFound.flashing("error" → s"Couldn't find post with id $id")
      case Some(pr) ⇒ Ok(views.html.posts.getEdit(id,postForm.fill(pr.post))) } }

  def edit(id: Long) = Action { implicit request ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getEdit(id,formWithErrors)).flashing(
          "error" → "There were errors with your submission.") },
      p ⇒ {
        run(Post.edit(id,p)) match {
          case None ⇒ Redirect(routes.PostController.getEdit(id)).flashing(
            "error" → "There was an error saving your edits. Please re-enter.")
          case Some(_) ⇒ Redirect(routes.PostController.list()).flashing(
            "success" → "Post edited!")}})}

  def delete(id: Long) = Action { implicit request ⇒
    run(Post.delete(id)) match {
      case None ⇒ Redirect(routes.PostController.list()).flashing(
        "error" → "There was an error deleting the post. Please try again.")
      case Some(_) ⇒ Redirect(routes.PostController.list()).flashing(
        "success" → "Post deleted.") } }

}
