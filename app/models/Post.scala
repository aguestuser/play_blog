package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import scalaz.Reader
import repos.PostRepo

/**
 * Author: @aguestuser
 * Date: 3/21/15
 */


case class Post(title: String, body: String)

object Post {

  implicit val postReads: Reads[Post] = (
    (JsPath \ "title").read[String](minLength[String](2)) and
      (JsPath \ "body").read[String](minLength[String](2))
    )(Post.apply _)

  implicit val postWrites: Writes[Post] = (
    (JsPath \ "title").write[String] and
      (JsPath \ "body").write[String]
    )(unlift(Post.unapply))

  def find(id: Long) = Reader { (repo: PostRepo) ⇒ repo.find(id) }
  def findAll = Reader { (repo: PostRepo) ⇒ repo.findAll }
  def create(p: Post) = Reader { (repo: PostRepo) ⇒ repo.create(p) }
  def edit(id: Long, edits: Post) = Reader { (repo: PostRepo) ⇒ repo.edit(id,edits) }
  def delete(id: Long) = Reader { (repo: PostRepo) ⇒ repo.delete(id) }

}