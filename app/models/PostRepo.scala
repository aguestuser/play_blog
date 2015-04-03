package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Format}

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */

trait PostRepo { // TODO make a higher-kinded Repo[_] type?
  def find(id: Long): Option[PostResource]
  def findAll: List[PostResource]
  def create(p: Post): Option[Long]
  def edit(id: Long, p: Post): Option[Int]
  def delete(id: Long): Option[Int]
}

case class PostResource(id: Long, post: Post) extends PostPayload

trait PostPayload // TODO use this as the return type of Post CRUD methods?
case class PostMaybeResource(maybe: Option[PostResource])
case class PostCollection(list: List[PostResource])
case class PostWrite(res: Option[Long])
case class PostUpdate(res: Option[Int])

object PostResource {
  implicit val postResourceFormat: Format[PostResource] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "post").format[Post]
    )(PostResource.apply, unlift(PostResource.unapply))
}