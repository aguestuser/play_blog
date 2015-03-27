package models

import play.api.libs.json._
import Reads._
import play.api.libs.functional.syntax._
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
