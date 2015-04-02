package models

import anorm.SqlParser._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
 * Author: @aguestuser
 * Date: 3/25/15
 */

case class PostRepo(id: Long, post: Post)

trait PostJson {
  implicit val postResourceFormat: Format[PostRepo] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "post").format[Post]
    )(PostRepo.apply, unlift(PostRepo.unapply))
}

object PostRepo extends dao.DbName with PostJson {

  import anorm._
  import play.api.Play.current
  import play.api.db.DB

  val postRepo = {
    get[Long]("id") ~ get[String]("title") ~ get[String]("body") map {
      case id ~ t ~ b => PostRepo(id,Post(t,b)) } }

  def find(id: Long): Option[PostRepo] =
    DB.withConnection(dbName) { implicit c =>
      SQL"select * from posts where id = $id"
        .as(postRepo *) match {
        case Nil ⇒ None
        case prr ⇒ Some(prr.head) } }

  def findAll: List[PostRepo] =
    DB.withConnection(dbName) { implicit c ⇒
      SQL"select * from posts"
        .as(postRepo *) } // TODO add "order by created asc"

  def create(p: Post): Option[Long] =
    Post.validate(p) flatMap { p ⇒
      DB.withConnection(dbName) { implicit c =>
        SQL"insert into posts (title,body) values (${p.title},${p.body})"
          .executeInsert() } }

  def edit(id: Long, p: Post): Option[Int] =
    Post.validate(p) flatMap { p ⇒
      optionify { DB.withConnection(dbName) { implicit c =>
        SQL"""update posts set title = ${p.title}, body = ${p.body} where id = $id"""
          .executeUpdate() } } }

  def delete(id: Long): Option[Int] =
    optionify { DB.withConnection(dbName) { implicit c =>
      SQL"delete from posts where id = $id"
        .executeUpdate() } }

  private def optionify(i: Int): Option[Int] = i match {
    case 0 ⇒ None
    case _ ⇒ Some(1)
  }
}


//
//trait PostRepooo
//case class PostResource(id: Long, post: Post) extends PostRepooo
//case class PostColl(l: List[PostRepo]) extends PostRepooo
//case class PostWrite(res: Int) extends PostRepooo
//case object PostNone extends PostRepooo