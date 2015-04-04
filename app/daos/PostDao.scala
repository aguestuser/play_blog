package daos

import anorm.SqlParser.get
import anorm._
import models.Post
import org.joda.time.DateTime
import repos.{PostRepo, PostResource}
import env.Environment

/**
 * Author: @aguestuser
 * Date: 3/25/15
 */

object PostDao extends Dao[Post,PostResource] with PostRepo {

  //TODO should i thread `db` and `now` from PostController to here via dependency inj?

  def db = Environment.db("default")
  def now = Environment.now

  val parse = {
    get[Long]("id") ~
      get[DateTime]("created") ~
      get[DateTime]("modified") ~
      get[String]("title") ~
      get[String]("body") map {
      case id ~ cr ~ mod ~ title ~ body ⇒
        PostResource(id,cr,mod,Post(title,body)) } }

  def findStatement(id: Long) =
    SQL"select * from posts where id = $id"

  def findAllStatement = SQL"select * from posts order by created asc"

  def createStatement(p: Post) =
    SQL"insert into posts (title,body,created,modified) values (${p.title},${p.body},$now,$now)"

  def editStatement(id: Long, p: Post) =
    SQL"update posts set title = ${p.title}, body = ${p.body}, modified = $now where id = $id"

  def deleteStatement(id: Long) =
    SQL"delete from posts where id = $id"

  def validate(p: Post): Option[Post] = {
    val conditions = List(
      p.title.length > 2,
      p.body.length > 2 )
    if ((true /: conditions)(_ && _)) Some(p) else None }

}