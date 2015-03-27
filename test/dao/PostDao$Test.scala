package dao

import anorm.SqlParser._
import anorm._
import models.Post
import org.specs2.mutable.{Before, Specification}
import org.specs2.specification.{Scope, BeforeEach}
import play.api.Play.current
import play.api.db.DB
import play.api.test.WithApplication

/**
 * Author: @aguestuser
 * Date: 3/26/15
 */


trait Setup extends WithApplication with Scope with Before {

  val posts = List(
    Post("first post", "what should i write?"),
    Post("second post", "i'm getting the hang of this"),
    Post("last post", "i think i'll try twitter"))

  val count = { get[Int]("count") }

  val ids: List[Option[Long]] = DB.withConnection { implicit c ⇒
    SQL"delete from posts".executeUpdate()
    for {
      p ← posts
      id ← SQL"insert into posts (title, body) values(${p.title}, ${p.body})".executeInsert()
    } yield id }
}

class PostDao$Test extends Specification with BeforeEach {

//  def before = DB.withConnection { implicit c ⇒
//    SQL"delete from posts".executeUpdate()
//    val ids = for {
//      p ← posts
//      id:Option[Long] ← SQL"insert into posts (title, body) values(${p.title}, ${p.body})".executeInsert()
//    } yield id }

//  def after = DB.withConnection { implicit c ⇒
//    SQL"delete from posts".execute() }

  "Post DAO" should {

    "find a post" in new Setup {
      PostDao.find(ids(0)) === Some(PostDao(1, Post("first post", "what should i write?")))
      PostDao.find(2) === Some(PostDao(2, Post("second post", "i'm getting the hang of this")))
      PostDao.find(3) === Some(PostDao(3, Post("last post", "i think i'll try twitter")))
    }

    "not find a non-existent post" in new Setup {
      PostDao.find(666) === None
    }

    "find all posts" in new Setup {
      PostDao.findAll === Some(List(
        PostDao(1, Post("first post", "what should i write?")),
        PostDao(2, Post("second post", "i'm getting the hang of this")),
        PostDao(3, Post("last post", "i think i'll try twitter"))))
    }

    "find no posts if there are none" in new Setup {
      DB.withConnection("test"){ implicit c ⇒ SQL"delete from posts".executeUpdate() }
      PostDao.findAll === Nil
    }

    "create a post" in new Setup {
      PostDao.create("second thoughts", "turns out i don't like twitter") === Some(4)

//      DB.withConnection("test"){ implicit c ⇒
//        SQL"select count(*) from posts".as(count *) } ===
//          4
    }

    "not create an improperly formatted post" in new Setup {
      PostDao.create("t","b") === None
//      DB.withConnection("test"){ implicit c ⇒
//        SQL"select count(*) from posts".as(count *) } ===
//          3
    }

    "edit a post" in new Setup {
      PostDao.edit(1, Post("changed my mind", "I Think I'll Try Capital Letters.")) === 1
      PostDao.find(1) === Post("changed my mind", "I Think I'll Try Capital Letters.")
    }

    "not save an edit that is improperly formatted" in new Setup {
      PostDao.edit(1, Post("a","b")) === 0
      PostDao.find(1) === PostDao(1, Post("first post", "what should i write?"))
    }

    "not edit a post that doesn't exist" in new Setup {
      PostDao.edit(666, Post("oh hai!", "this probably wont' get written")) === 0
    }

    "delete a post" in new Setup {
      PostDao.delete(3) === 1
//      DB.withConnection("test"){ implicit c ⇒
//        SQL"select count(*) from posts".as(count *) } ===
//          2
    }

    "not delete a post that does't exist" in new Setup {
      PostDao.delete(666) === 0
//      DB.withConnection("test"){ implicit c ⇒
//        SQL"select count(*) from posts".as(count *) } ===
//          3
    }

  }

}
