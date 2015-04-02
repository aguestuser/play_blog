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

  implicit val postReads: Reads[Post] = (
    (JsPath \ "title").read[String](minLength[String](2)) and
      (JsPath \ "body").read[String](minLength[String](2))
    )(Post.apply _)

  implicit val postWrites: Writes[Post] = (
    (JsPath \ "title").write[String] and
      (JsPath \ "body").write[String]
    )(unlift(Post.unapply))

  def validate(p: Post): Option[Post] = {
    val conditions = List(
      p.title.length > 2,
      p.body.length > 2
    )
    if ((true /: conditions)(_ && _)) Some(p) else None
  }

  def queryString(p: Post): String = s"?title=${encode(p.title)}&body=${encode(p.body)}"

}