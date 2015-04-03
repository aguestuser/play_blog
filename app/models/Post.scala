package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import com.ning.http.util.UTF8UrlEncoder.encode

/**
 * Author: @aguestuser
 * Date: 3/21/15
 */


case class Post(title: String, body: String)

object Post {

  import scalaz.Reader

  implicit val postReads: Reads[Post] = (
    (JsPath \ "title").read[String](minLength[String](2)) and
      (JsPath \ "body").read[String](minLength[String](2))
    )(Post.apply _)

  implicit val postWrites: Writes[Post] = (
    (JsPath \ "title").write[String] and
      (JsPath \ "body").write[String]
    )(unlift(Post.unapply))

  // TODO add types to these Readers?
  def find(id: Long) = Reader { (repo: PostRepo) ⇒ repo.find(id) }
  def findAll = Reader { (repo: PostRepo) ⇒ repo.findAll }
  def create(p: Post) = Reader { (repo: PostRepo) ⇒ repo.create(p) }
  def edit(id: Long, edits: Post) = Reader { (repo: PostRepo) ⇒ repo.edit(id,edits) }
  def delete(id: Long) = Reader { (repo: PostRepo) ⇒ repo.delete(id) }


  def validate(p: Post): Option[Post] = {
    val conditions = List(
      p.title.length > 2,
      p.body.length > 2
    )
    if ((true /: conditions)(_ && _)) Some(p) else None
  }

  def queryString(p: Post): String = s"?title=${encode(p.title)}&body=${encode(p.body)}"

}