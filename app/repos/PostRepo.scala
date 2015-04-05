package repos

import anorm.SqlParser.{get ⇒ parse}
import models.Post
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}


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

  import util.Date.ppDateWrites

  implicit val readsPostRepo: Reads[PostResource] =
    ((JsPath \ "id").read[Long] and
      (JsPath \ "created").read[DateTime] and
      (JsPath \ "modified").read[DateTime] and
      (JsPath \ "post").read[Post]
      )(PostResource.apply _)

  implicit val writesPostRepo: Writes[PostResource] =
    ((JsPath \ "id").write[Long] and
      (JsPath \ "created").write[DateTime] and
      (JsPath \ "modified").write[DateTime] and
      (JsPath \ "post").write[Post]
      )(unlift(PostResource.unapply))
}