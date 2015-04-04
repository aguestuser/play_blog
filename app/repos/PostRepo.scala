package repos

import anorm.SqlParser.{get ⇒ parse}
import models.Post
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */


trait PostRepo extends Repo[Post,PostResource]
case class PostResource(id: Long, post: Post) extends RepoResource[Post]

object PostRepo {
  implicit val formatPostRepo: Format[PostResource] = (
    (JsPath \ "id").format[Long] and
    (JsPath \ "post").format[Post]
  )(PostResource.apply, unlift(PostResource.unapply))
}