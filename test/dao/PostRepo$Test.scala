package dao


import anorm._
import models.{Post, PostRepo}
import org.specs2.execute.AsResult
import org.specs2.mutable.Specification
import org.specs2.specification.After
import play.api.db.DB
import support.FakeORamaPosts
import support.posts.WithFakePosts


class PostRepo$Test extends Specification with FakeORamaPosts {

  "Post DAO" should {

    "find a post" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)

        def run = this {
          PostRepo.find(ids.head) === Some(PostRepo(ids.head, posts.head))
          PostRepo.find(ids(1)) === Some(PostRepo(ids(1), posts(1)))
          PostRepo.find(ids(2)) === Some(PostRepo(ids(2), posts(2))) } }

      AsResult.effectively(ex().run)
    }

    "not find a non-existent post" >> new WithFakePosts {
      PostRepo.find(666) === None
    }

    "find all posts" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostRepo.findAll === List( // TODO make a contains test?
            PostRepo(ids.head, posts.head),
            PostRepo(ids(1), posts(1)),
            PostRepo(ids(2), posts(2))) } }

      AsResult.effectively(ex().run)
    }

    "find no posts if there are none" >> new WithFakePosts {
      DB.withConnection("test"){ implicit c ⇒ SQL"delete from posts".executeUpdate() }
      PostRepo.findAll === Nil
    }

    "create a post" >> new WithFakePosts {

      case class ex() extends After {
        val id = PostRepo.create(Post("second thoughts", "turns out i don't like twitter"))
        def after = teardown(List(id.get))
        def run = this {

          id must beSome
          PostRepo.find(id.get) === Some(PostRepo(id.get, Post("second thoughts", "turns out i don't like twitter"))) } }

      AsResult.effectively(ex().run)
    }

    "not create an improperly formatted post" >> new WithFakePosts {
      PostRepo.create(Post("t","b")) === None
    }

    "edit a post" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostRepo.edit(ids.head, Post("changed my mind", "I Think I'll Try Capital Letters.")) === Some(1)
          PostRepo.find(ids.head) === Some(PostRepo(ids.head,Post("changed my mind", "I Think I'll Try Capital Letters."))) } }

     AsResult.effectively(ex().run)
    }

    "not save an edit that is improperly formatted" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostRepo.edit(ids.head, Post("a","b")) === None
          PostRepo.find(ids.head) === Some(PostRepo(ids.head, posts.head)) } }

      AsResult.effectively(ex().run)
    }

    "not edit a post that doesn't exist" >> new WithFakePosts {
      PostRepo.edit(666, Post("oh hai!", "this probably wont' get written")) === None
    }

    "delete a post" >> new WithFakePosts {

      case class ex() extends After {
        val ids = setup(posts)
        def after = teardown(ids)
        def run = this {

          PostRepo.delete(ids.head) === Some(1)
          PostRepo.find(ids.head) === None } }

      AsResult.effectively(ex().run)
    }

    "not delete a post that does't exist" >> new WithFakePosts {
      PostRepo.delete(666) === None
    }
  }
}


