package repos

import anorm.SqlParser.{get ⇒ parse}
import models.Post
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */


trait PostRepo extends Repo[Post,PostResource]
case class PostResource(
                         id: Long,
                         created: DateTime,
                         modified: DateTime,
                         post: Post) extends RepoResource[Post]

object PostRepo {
  implicit val formatPostRepo: Format[PostResource] =
    ((JsPath \ "id").format[Long] and
      (JsPath \ "created").format[DateTime] and
      (JsPath \ "modified").format[DateTime] and
      (JsPath \ "post").format[Post]
      )(PostResource.apply, unlift(PostResource.unapply))
}