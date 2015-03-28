package dao

import models.Post

import play.api.libs.json._
import Reads._
import play.api.libs.functional.syntax._

/**
 * Author: @aguestuser
 * Date: 3/25/15
 */

case class PostDao(id: Long, post: Post)

object PostDao extends DbName {

  import anorm.SqlParser._
  import anorm._
  import play.api.Play.current
  import play.api.db.DB

  val pr = {
    get[Long]("id") ~ get[String]("title") ~ get[String]("body") map {
      case id ~ t ~ b => PostDao(id,Post(t,b)) } }

  implicit val postRepoReads: Reads[PostDao] = (
      (JsPath \ "id").read[Long] and
      (JsPath \ "post").read[Post]
    )(PostDao.apply _)

  implicit val postRepoWrites: Writes[PostDao] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "post").write[Post]
    )(unlift(PostDao.unapply))

  def find(id: Long): Option[PostDao] =
    DB.withConnection(dbName) { implicit c =>
      SQL"select * from posts where id = $id"
        .as(pr *) match {
          case Nil ⇒ None
          case prr ⇒ Some(prr.head) } }

  def findAll: List[PostDao] =
    DB.withConnection(dbName) { implicit c =>
      SQL"select * from posts"
        .as(pr *) } // TODO add "order by created asc"

  def create(title: String, body: String): Option[Long] =
    Post.validate(Post(title,body)) flatMap { p ⇒
      DB.withConnection(dbName) { implicit c =>
        SQL"insert into posts (title,body) values (${p.title},${p.body})"
          .executeInsert() } }

  def edit(id: Long, np: Post): Option[Int] =
    Post.validate(np) flatMap { p ⇒
      optionify { DB.withConnection(dbName) { implicit c =>
        SQL"""update posts set title = ${p.title}, body = ${p.body} where id = $id"""
          .executeUpdate() } } }

  def delete(id: Long): Option[Int] =
    optionify { DB.withConnection(dbName) { implicit c =>
      SQL"delete from posts where id = $id"
        .executeUpdate() } }

  private def optionify(i: Int): Option[Int] = i match {
    case 0 ⇒ None
    case 1 ⇒ Some(1)
  }

}
