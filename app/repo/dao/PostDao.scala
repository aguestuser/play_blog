package repo.dao


import models.Post
import repo.{PostRepo, PostResource}

/**
 * Author: @aguestuser
 * Date: 3/25/15
 */

object PostDao extends PostRepo with DbName {

  import anorm._
  import play.api.Play.current
  import play.api.db.DB

  def find(id: Long): Option[PostResource] =
    DB.withConnection(dbName) { implicit c =>
      SQL"select * from posts where id = $id"
        .as(sql_row *) match {
        case Nil ⇒ None
        case prr ⇒ Some(prr.head) } }

  def findAll: List[PostResource] =
    DB.withConnection(dbName) { implicit c ⇒
      SQL"select * from posts"
        .as(sql_row *) } // TODO add "order by created asc"

  def create(p: Post): Option[Long] =
    validate(p) flatMap { p ⇒
      DB.withConnection(dbName) { implicit c =>
        SQL"insert into posts (title,body) values (${p.title},${p.body})"
          .executeInsert() } }

  def edit(id: Long, p: Post): Option[Int] =
    validate(p) flatMap { p ⇒
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