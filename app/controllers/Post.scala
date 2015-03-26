package controllers

import models.PostData
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}


/**
 * Author: @aguestuser
 * Date: 3/21/15
 */

object Post extends Controller {

  val postForm = Form {
    mapping(
      "title" → text(minLength = 2),
      "body" → text(minLength = 2)
    )(PostData.apply)(PostData.unapply) }

//  val editPostForm = Form {
//    mapping(
//      "id" → longNumber,
//      "title" ->  text(minLength = 2),
//      "body" ->  text(minLength = 2)
//    )(PostRepo.apply)(PostRepo.unapply) }

  def show(id: Long) = Action { implicit request =>
    dao.Post.find(id) match {
      case None => NotFound.flashing("error" → s"Couldn't find a contact with id $id")
      case Some(p) => Ok(views.html.posts.show(p)) } }

  def list = Action { implicit request =>
    dao.Post.list match {
      case Nil => NotFound.flashing("error" → "There were no posts to list!")
      case ps => Ok(views.html.posts.list(ps)) } }

  def getCreate = Action { implicit request ⇒
    Ok(views.html.posts.getCreate(postForm)) }

  def create = Action { implicit request ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getCreate(formWithErrors)).flashing(
          "error" → "There were errors with your submission.") },
      p ⇒ {
        dao.Post.create(p.title,p.body) match {
          case None ⇒ NotFound.flashing("error" → "Whoops!")
          case Some(id) ⇒ Redirect(routes.Post.list()).flashing(
            "success" → "Post created!") } }) }

  def getEdit(id: Long) = Action { implicit request ⇒
    dao.Post.find(id) match {
      case None ⇒ NotFound.flashing("error" → s"Couldn't find post with id $id")
      case Some(pr) ⇒ Ok(views.html.posts.getEdit(id,postForm.fill(pr.toData))) } }

  def edit(id: Long) = Action { implicit request ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getEdit(id,formWithErrors)).flashing(
          "error" → "There were errors with your submission.") },
      p ⇒ {
        dao.Post.edit(id,p) match {
          case 1 ⇒ Redirect(routes.Post.list()).flashing(
            "success" → "Post edited!") } }) }

  def delete(id: Long) = Action { implicit request ⇒
    dao.Post.delete(id) match {
      case 1 ⇒ Redirect(routes.Post.list()).flashing("success" → "Post deleted.") } }
}
