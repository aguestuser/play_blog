package controllers

//import models.PostData

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}



/**
 * Author: @aguestuser
 * Date: 3/21/15
 */

object PostController extends Controller {

  val postForm = Form {
    mapping(
      "title" → text(minLength = 2),
      "body" → text(minLength = 2)
    )(models.Post.apply)(models.Post.unapply) }

  def show(id: Long) = Action { implicit request =>
    dao.PostDao.find(id) match {
      case None => NotFound.flashing("error" → s"Couldn't find a contact with id $id")
      case Some(pr) => Ok(views.html.posts.show(pr)) } }

  def getOne(id: Long) = Action { implicit request ⇒
    dao.PostDao.find(id) match {
      case None => NotFound.flashing("error" → s"Couldn't find a contact with id $id")
      case Some(pr) => Ok(Json.toJson(pr)) } }

  def list = Action { implicit request =>
    Ok(views.html.posts.list("Posts")) }

  def getAll = Action { implicit request ⇒
    dao.PostDao.findAll match {
      case Nil => NotFound.flashing("error" → "There were no posts to list!")
      case ps => Ok(Json.toJson(ps)) } }

  def getCreate = Action { implicit request ⇒
    Ok(views.html.posts.getCreate(postForm)) }

  def create = Action { implicit request ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getCreate(formWithErrors)).flashing(
          "error" → "There were errors with your submission.") },
      p ⇒ {
        dao.PostDao.create(p.title,p.body) match {
          case None ⇒ NotFound.flashing("error" → "Whoops!")
          case Some(id) ⇒ Redirect(routes.PostController.list()).flashing(
            "success" → "Post created!") } }) }

  def getEdit(id: Long) = Action { implicit request ⇒
    dao.PostDao.find(id) match {
      case None ⇒ NotFound.flashing(
        "error" → s"Couldn't find post with id $id")
      case Some(pr) ⇒ Ok(views.html.posts.getEdit(id,postForm.fill(pr.post))) } }

  def edit(id: Long) = Action { implicit request ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getEdit(id,formWithErrors)).flashing(
          "error" → "There were errors with your submission.") },
      p ⇒ {
        dao.PostDao.edit(id,p) match {
          case 1 ⇒ Redirect(routes.PostController.list()).flashing(
            "success" → "Post edited!") } }) }

  def delete(id: Long) = Action { implicit request ⇒
    dao.PostDao.delete(id) match {
      case 1 ⇒ Redirect(routes.PostController.list()).flashing(
        "success" → "Post deleted.") } }


}
