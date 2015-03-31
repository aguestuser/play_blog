package models

import play.api.libs.json._
import Reads._
import play.api.libs.functional.syntax._

import scalaz.Reader

/**
 * Author: @aguestuser
 * Date: 3/21/15
 */


case class Post(title: String, body: String)

object Post {

  implicit val postReads: Reads[Post] = (
    (JsPath \ "title").read[String](minLength[String](2)) and
      (JsPath \ "body").read[String](minLength[String](2))
    )(Post.apply _)

  implicit val postWrites: Writes[Post] = (
    (JsPath \ "title").write[String] and
      (JsPath \ "body").write[String]
    )(unlift(Post.unapply))

  def validate(p: Post): Option[Post] = {
    val conditions = List(
      p.title.length > 2,
      p.body.length > 2
    )
    if ((true /: conditions)(_ && _)) Some(p) else None
  }

  def find(id: Long) = Reader { (repo: PostRepoImpl) ⇒ repo.find(id) }
  def findAll = Reader { (repo: PostRepoImpl) ⇒ repo.findAll }
  def create(p: Post) = Reader { (repo: PostRepoImpl) ⇒ repo.create(p) }
  def edit(id: Long, edits: Post) = Reader { (repo: PostRepoImpl) ⇒ repo.edit(id,edits) }
  def delete(id: Long) = Reader { (repo: PostRepoImpl) ⇒ repo.delete(id) }


}

//trait Post
//case class PostData(title: String, body: String) extends Post
//case class PostRepo(id: Long, title: String, body: String) extends Post {
//  def toData: PostData = PostData(title,body)
//}



//trait PostRepo {
//
//  def find(id: Long): Option[Post]
//  def list: List[Post]
//  def create(title: String, body: String): Option[Post]
//  def edit(id: Long, edits: Post): Option[Post]
//  def delete(id: Long): Int
//
//}


//trait PostMock extends PostRepo { ??? }
//
//class PostService(in: PostSource, out: PostRepo) { }
