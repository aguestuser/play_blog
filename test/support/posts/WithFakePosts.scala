package support.posts

import anorm._
import SqlParser._
import models.{Post, PostRepo}
import org.specs2.specification.Scope
import play.api.db.DB
import play.api.test.WithApplication


/**
* Author: @aguestuser
* Date: 3/26/15
*/

trait WithFakePosts extends WithApplication with Scope {



  val posts = List(
    Post("first post", "what should i write?"),
    Post("second post", "i'm getting the hang of this"),
    Post("last post", "i think i'll try twitter"))

  val postRepos = (1 to 3) map { n ⇒ PostRepo(n,posts(n-1)) } toList

  val count = { get[Int]("count") }
  val id = { get[Long]("id") }

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

  lazy val last: List[Long] =
    DB.withConnection("test") { implicit c ⇒
      SQL"select id from posts order by id desc limit 1"
        .as(id *) }

  def numPosts: Int =
    DB.withConnection("test") { implicit c ⇒
      SQL"select count(*) from posts"
        .as(count *).head }

  lazy val createPostHtml = "\n\n\n<h1>New Post</h1>\n \n\n<form action=\"/posts/create/\" method=\"POST\" >\n    \n    \n\n\n\n\n\n\n\n\n\n<dl class=\" \" id=\"title_field\">\n    \n    <dt><label for=\"title\">title</label></dt>\n    \n    <dd>\n    <input type=\"text\" id=\"title\" name=\"title\" value=\"\" />\n</dd>\n    \n    \n        <dd class=\"info\">Minimum length: 2</dd>\n    \n</dl>\n\n\n\n    \n\n\n\n\n\n\n\n<dl class=\" \" id=\"body_field\">\n    \n    <dt><label for=\"body\">body</label></dt>\n    \n    <dd>\n    <textarea id=\"body\" name=\"body\" ></textarea>\n</dd>\n    \n    \n        <dd class=\"info\">Minimum length: 2</dd>\n    \n</dl>\n\n\n\n    <input type=\"submit\" value=\"Save\">\n\n</form>\n\n"
}
