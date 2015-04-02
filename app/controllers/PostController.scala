package controllers

import java.util.Locale

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future


/**
 * Author: @aguestuser
 * Date: 3/21/15
 */


object PostController extends Controller with PostJson {

  //parsers
  val postForm = Form {
    mapping(
      "title" → text(minLength = 2),
      "body" → text(minLength = 2)
    )(Post.apply)(Post.unapply) }

  val formOrJson = parse.using { req ⇒
    req.contentType.map(_.toLowerCase(Locale.ENGLISH)) match {
      case Some("application/json") | Some("text/json") => play.api.mvc.BodyParsers.parse.json
      case Some("application/x-www-form-urlencoded") ⇒ play.api.mvc.BodyParsers.parse.urlFormEncoded
      case _ ⇒ play.api.mvc.BodyParsers.parse.error(Future.successful(UnsupportedMediaType("Invalid content type specified"))) } }

  //show
  def show(id: Long) = Action { Ok(views.html.posts.show("Post", id)) }
  def getOne(id: Long) = Action {
    PostRepo.find(id) match {
      case None ⇒ Ok(Json.obj())
      case Some(pr) ⇒ Ok(Json.toJson(pr)) } }

  //list
  def list = Action { Ok(views.html.posts.list("Posts")) }
  def getAll = Action { Ok(Json.toJson(PostRepo.findAll)) }

  //create
  def getCreate = Action { Ok(views.html.posts.getCreate(postForm)) }

  def create = Action(parse.urlFormEncoded) { implicit req ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getCreate(formWithErrors))},
      p ⇒ {
        PostRepo.create(p)
        Redirect(routes.PostController.list()).flashing("success" → "Post created") } ) }

  def createJson = Action(parse.json) { implicit req ⇒
    req.body.validate[Post].fold(
      errors ⇒ {
        BadRequest(Json.obj("status" → "KO", "message" -> JsError.toFlatJson(errors))) },
      p ⇒ {
        val id = PostRepo.create(p).get
        Ok(Json.obj("status" → "Ok", "message" → s"Post created")) }) }

  //edit
  def getEdit(id: Long) = Action {
    PostRepo.find(id) match {
      case None ⇒ NotFound.flashing("error" → s"Couldn't find post with id $id")
      case Some(pr) ⇒ Ok(views.html.posts.getEdit(id,postForm.fill(pr.post))) } }


  def edit(id: Long) = Action(parse.urlFormEncoded){ implicit req ⇒
    postForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        BadRequest(views.html.posts.getEdit(id,formWithErrors)).flashing("error" → "Try again.") },
      p ⇒ {
        PostRepo.edit(id,p)
        Redirect(routes.PostController.list()).flashing("success" → "Post edited")}) }

  def editJson(id: Long) = Action(parse.json){ implicit req ⇒
    req.body.validate[Post].fold(
      errors ⇒ {
        BadRequest(Json.obj("status" → "KO", "message" -> JsError.toFlatJson(errors))) },
      p ⇒ {
        PostRepo.edit(id,p)
        Ok(Json.obj("status" → "Ok", "message" → "Post edited")) } ) }

  //delete
  def delete(id: Long) = Action { implicit req ⇒
    PostRepo.delete(id) match {
      case None ⇒ Redirect(routes.PostController.list()).flashing(
        "error" → "You tried to delete a post that doesn't exist")
      case Some(_) ⇒ Redirect(routes.PostController.list()).flashing(
        "success" → "Post deleted") } }


}
