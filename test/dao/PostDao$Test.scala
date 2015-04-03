package dao


import anorm._
import models.{Post, PostDao, PostResource}
import org.specs2.execute.AsResult
import org.specs2.mutable.Specification
import org.specs2.specification.After
import play.api.db.DB
import support.posts.WithFakePosts


class PostDao$Test extends Specification {

  "Post DAO" should {

    "find a post" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)

        def run = this {
          PostDao.find(ids.head) === Some(PostResource(ids.head, posts.head))
          PostDao.find(ids(1)) === Some(PostResource(ids(1), posts(1)))
          PostDao.find(ids(2)) === Some(PostResource(ids(2), posts(2))) } }

      AsResult.effectively(ex().run)
    }

    "not find a non-existent post" >> new WithFakePosts {
      PostDao.find(666) === None
    }

    "find all posts" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.findAll === List( // TODO make a contains test?
            PostResource(ids.head, posts.head),
            PostResource(ids(1), posts(1)),
            PostResource(ids(2), posts(2))) } }

      AsResult.effectively(ex().run)
    }

    "find no posts if there are none" >> new WithFakePosts {
      DB.withConnection("test"){ implicit c ⇒ SQL"delete from posts".executeUpdate() }
      PostDao.findAll === Nil
    }

    "create a post" >> new WithFakePosts {

      case class ex() extends After {
        val id = PostDao.create(Post("second thoughts", "turns out i don't like twitter"))
        def after = teardown(List(id.get))
        def run = this {

          id must beSome
          PostDao.find(id.get) === Some(PostResource(id.get, Post("second thoughts", "turns out i don't like twitter"))) } }

      AsResult.effectively(ex().run)
    }

    "not create an improperly formatted post" >> new WithFakePosts {
      PostDao.create(Post("t","b")) === None
    }

    "edit a post" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.edit(ids.head, Post("changed my mind", "I Think I'll Try Capital Letters.")) === Some(1)
          PostDao.find(ids.head) === Some(PostResource(ids.head,Post("changed my mind", "I Think I'll Try Capital Letters."))) } }

     AsResult.effectively(ex().run)
    }

    "not save an edit that is improperly formatted" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.edit(ids.head, Post("a","b")) === None
          PostDao.find(ids.head) === Some(PostResource(ids.head, posts.head)) } }

      AsResult.effectively(ex().run)
    }

    "not edit a post that doesn't exist" >> new WithFakePosts {
      PostDao.edit(666, Post("oh hai!", "this probably wont' get written")) === None
    }

    "delete a post" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostDao.delete(ids.head) === Some(1)
          PostDao.find(ids.head) === None } }

      AsResult.effectively(ex().run)
    }

    "not delete a post that does't exist" >> new WithFakePosts {
      PostDao.delete(666) === None
    }
  }
}


