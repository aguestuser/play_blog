package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Format}

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */

trait PostRepo {
  def find(id: Long): Option[PostResource]
  def findAll: List[PostResource]
  def create(p: Post): Option[Long]
  def edit(id: Long, p: Post): Option[Int]
  def delete(id: Long): Option[Int]
}

case class PostResource(id: Long, post: Post)

object PostResource {
  implicit val postResourceFormat: Format[PostResource] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "post").format[Post]
    )(PostResource.apply, unlift(PostResource.unapply))
}