package repo.dao

import anorm.SqlParser.{get ⇒ parse}
import anorm._
import models.Post
import repo.{PostRepo, PostResource}

/**
 * Author: @aguestuser
 * Date: 3/25/15
 */

object PostDao extends Dao[Post,PostResource] with PostRepo {

  val table_name = "posts"

  val sql_row = {
    parse[Long]("id") ~ parse[String]("title") ~ parse[String]("body") map {
      case id ~ t ~ b ⇒
        PostResource(id,Post(t,b)) } }

  def createStatement(p: Post) =
    SQL"insert into posts (title,body) values (${p.title},${p.body})"

  def editStatement(id: Long, p: Post) =
    SQL"update posts set title = ${p.title}, body = ${p.body} where id = $id"

  def validate(p: Post): Option[Post] = {
    val conditions = List(
      p.title.length > 2,
      p.body.length > 2 )
    if ((true /: conditions)(_ && _)) Some(p) else None }

}