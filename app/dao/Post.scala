package dao

import models.PostRepo

/**
 * Author: @aguestuser
 * Date: 3/25/15
 */

object Post {

  import anorm.SqlParser._
  import anorm._
  import play.api.Play.current
  import play.api.db.DB

  val pr = {
    get[Long]("id") ~ get[String]("title") ~ get[String]("body") map {
      case i ~ t ~ b => models.PostRepo(i,t,b) } }

  val pd = {
    get[String]("title") ~ get[String]("body") map {
      case t ~ b ⇒ models.PostData(t,b) } }

//  def find(p: Post): Option[Post] = find(p.id)
  def find(id: Long): Option[PostRepo] =
    DB.withConnection { implicit c =>
      SQL"select * from posts where id = {id}"
        .as(pr *) match {
          case Nil ⇒ None
          case p ⇒ Some(p.head) } }

  def list: List[PostRepo] =
    DB.withConnection { implicit c =>
      SQL("select * from posts")
        .as(pr *) } // TODO add "order by created asc"

  def create(title: String, body: String): Option[Long] =
    DB.withConnection { implicit c =>
      SQL"insert into posts (title,body) values ($title,$body)"
        .executeInsert() }

  def edit(id: Long, p: PostRepo): Int =
    DB.withConnection { implicit c =>
      SQL"""update posts set title = ${p.title}, body = ${p.body} where id = ${p.id}"""
        .executeUpdate() }

  def delete(id: Long): Int =
    DB.withConnection { implicit c =>
      SQL"delete from posts where id = ${id}"
        .executeUpdate() }

}
