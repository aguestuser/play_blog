package daos

import anorm.SqlParser.get
import anorm._
import models.Post
import repos.{PostRepo, PostResource}

/**
 * Author: @aguestuser
 * Date: 3/25/15
 */

object PostDao extends Dao[Post,PostResource] with PostRepo with DbName {

  val parse: RowParser[PostResource] = {
    get[Long]("id") ~ get[String]("title") ~ get[String]("body") map {
      case id ~ t ~ b ⇒
        PostResource(id,Post(t,b)) } }

  def findStatement(id: Long) =
    SQL"select * from posts where id = $id"

  def findAllStatement = SQL"select * from posts"

  def createStatement(p: Post) =
    SQL"insert into posts (title,body) values (${p.title},${p.body})"

  def editStatement(id: Long, p: Post) =
    SQL"update posts set title = ${p.title}, body = ${p.body} where id = $id"

  def deleteStatement(id: Long) =
    SQL"delete from posts where id = $id"

  def validate(p: Post): Option[Post] = {
    val conditions = List(
      p.title.length > 2,
      p.body.length > 2 )
    if ((true /: conditions)(_ && _)) Some(p) else None }

}