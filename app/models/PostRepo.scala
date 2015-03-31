package models

import anorm.SqlParser._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads}

/**
 * Author: @aguestuser
 * Date: 3/25/15
 */


case class PostRepo(id: Long, post: Post)

trait PostRepoImpl {

  implicit val postRepoReads: Reads[PostRepo] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "post").read[Post]
    )(PostRepo.apply _)

  implicit val postRepoWrites: Writes[PostRepo] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "post").write[Post]
    )(unlift(PostRepo.unapply))

  def find(id:Long): Option[PostRepo]
  def findAll: List[PostRepo]
  def create(p: Post): Option[Long]
  def edit(id: Long, p: Post): Option[Int]
  def delete(id: Long): Option[Int]
}

object PostDao extends dao.DbName with PostRepoImpl {

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
    case 1 ⇒ Some(1)
  }

}
