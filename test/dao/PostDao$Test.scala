package dao

import anorm.SqlParser._
import anorm._
import models.Post
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Specification
import org.specs2.specification.{After, Scope}
import play.api.db.DB
import play.api.test.WithApplication

/**
 * Author: @aguestuser
 * Date: 3/26/15
 */


trait WithDb extends WithApplication with Scope {

  val count = { get[Int]("count") }
  val posts = List(
    Post("first post", "what should i write?"),
    Post("second post", "i'm getting the hang of this"),
    Post("last post", "i think i'll try twitter"))

  override def around[R: AsResult](callback: ⇒ R): Result = super.around {
    AsResult.effectively(callback) }

  def setup(ps: List[Post]): List[Long] = {
    val res:List[Option[Long]] = DB.withConnection { implicit c ⇒
      ps map { p ⇒
        SQL"insert into posts (title, body) values(${p.title}, ${p.body})"
          .executeInsert() } }
    res.map(_.get) }

  def teardown(ids: List[Long]): List[Int] =
    DB.withConnection("test") { implicit c ⇒
      ids map { id ⇒
        SQL"delete from posts where id = $id"
          .executeUpdate() } }
}

class PostDao$Test extends Specification {

  "Post DAO" should {

    "find a post" >> new WithDb {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.find(ids.head) === Some(posts.head)
          PostDao.find(ids(1)) === Some(posts(1))
          PostDao.find(ids(2)) === Some(posts(2)) } }

      ex().run
    }

    "not find a non-existent post" >> new WithDb { 
      PostDao.find(666) === None
    }

    "find all posts" >> new WithDb {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.findAll === Some(List(
            PostDao(1, Post("first post", "what should i write?")),
            PostDao(2, Post("second post", "i'm getting the hang of this")),
            PostDao(3, Post("last post", "i think i'll try twitter")))) } }

      ex().run
    }

    "find no posts if there are none" >> new WithDb {
      DB.withConnection("test"){ implicit c ⇒ SQL"delete from posts".executeUpdate() }
      PostDao.findAll === Nil
    }

    "create a post" >> new WithDb {

      case class ex() extends After {
        val id = PostDao.create("second thoughts", "turns out i don't like twitter")
        def after = teardown(List(id.get))
        def run = this {

          id must beSome
          PostDao.find(id.get) === Some(PostDao(id.get, Post("second thoughts", "turns out i don't like twitter"))) } }

      ex().run
    }

    "not create an improperly formatted post" >> new WithDb {  
      PostDao.create("t","b") === None
    }

    "edit a post" >> new WithDb {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.edit(1, Post("changed my mind", "I Think I'll Try Capital Letters.")) === 1
          PostDao.find(1) === Post("changed my mind", "I Think I'll Try Capital Letters.") } }

      ex().run
    }

    "not save an edit that is improperly formatted" >> new WithDb {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.edit(ids.head, Post("a","b")) === 0
          PostDao.find(ids.head) === PostDao(ids.head, Post("first post", "what should i write?")) } }

      ex().run
    }

    "not edit a post that doesn't exist" >> new WithDb {  
      PostDao.edit(666, Post("oh hai!", "this probably wont' get written")) === 0
    }

    "delete a post" >> new WithDb {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.delete(ids.head) === 1
          PostDao.find(ids.head) === None } }

      ex().run
    }

    "not delete a post that does't exist" >> new WithDb {  
      PostDao.delete(666) === 0
    }

  }

}
