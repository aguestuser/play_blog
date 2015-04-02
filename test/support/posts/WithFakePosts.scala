package support.posts

import anorm.SqlParser._
import anorm._
import models.Post
import org.specs2.specification.Scope
import play.api.db.DB
import play.api.test.WithApplication


/**
* Author: @aguestuser
* Date: 3/26/15
*/


trait WithFakePosts extends WithApplication with Scope {

  lazy val post = Post("first post", "what should i write?")

  lazy val posts = List(
    Post("first post", "what should i write?"),
    Post("second post", "i'm getting the hang of this"),
    Post("last post", "i think i'll try twitter"))

  val count = { get[Int]("count") }
  val id = { get[Long]("id") }

  lazy val last: List[Long] =
    DB.withConnection("test") { implicit c ⇒
      SQL"select id from posts order by id desc limit 1"
        .as(id *) }

  def setup(ps: List[Post]): List[Long] = {
    val res:List[Option[Long]] = DB.withConnection("test") { implicit c ⇒
      ps map { p ⇒
        SQL"insert into posts (title, body) values(${p.title}, ${p.body})"
          .executeInsert() } }
    res.map(_.get) }

  def teardown(ids: List[Long]): List[Int] =
    DB.withConnection("test") { implicit c ⇒
      ids map { id ⇒
        SQL"delete from posts where id = $id"
          .executeUpdate() } }

  def numPosts: Int =
    DB.withConnection("test") { implicit c ⇒
      SQL"select count(*) from posts"
        .as(count *).head }

}
