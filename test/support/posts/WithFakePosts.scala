package support.posts

import anorm.SqlParser._
import anorm._
import models.Post
import org.specs2.specification.Scope
import play.api.db.DB
import play.api.test.WithApplication
import util.Environment


/**
* Author: @aguestuser
* Date: 3/26/15
*/


trait WithFakePosts extends WithApplication with SamplePosts with Scope {

  val count = { get[Int]("count") }
  val idd = { get[Long]("id") }
  lazy val now = Environment.s17
  val hmm = "hmmm"

  lazy val last: List[Long] =
    DB.withConnection("test") { implicit c ⇒
      SQL"select id from posts order by id desc limit 1"
        .as(idd *) }

  def setup(ps: List[Post]): List[Long] = {
    val res:List[Option[Long]] = DB.withConnection("test") { implicit c ⇒
      ps map { p ⇒
        SQL"insert into posts (title, body, created, modified) values(${p.title}, ${p.body}, $now, $now)"
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
