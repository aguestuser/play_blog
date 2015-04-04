package repo

import anorm.SqlParser.{get ⇒ parse}
import anorm.{RowParser, ~}
import models.Post
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Format}

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */


trait PostRepo extends Repo[Post,PostResource] {

  val table_name: String = "posts"

  val sql_row: RowParser[PostResource] = {
    parse[Long]("id") ~ parse[String]("title") ~ parse[String]("body") map {
      case id ~ t ~ b ⇒ PostResource(id,Post(t,b)) } }

  implicit val json: Format[PostResource] = (
    (JsPath \ "id").format[Long] and
    (JsPath \ "post").format[Post]
  )(PostResource.apply, unlift(PostResource.unapply))

  def validate(p: Post): Option[Post] = {
    val conditions = List(
      p.title.length > 2,
      p.body.length > 2
    )
    if ((true /: conditions)(_ && _)) Some(p) else None }

}

case class PostResource(id: Long, post: Post) extends RepoResource[Post]