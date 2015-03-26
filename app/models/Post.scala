package models

/**
 * Author: @aguestuser
 * Date: 3/21/15
 */

trait Post
case class PostData(title: String, body: String) extends Post
case class PostRepo(id: Long, title: String, body: String) extends Post {
  def toData: PostData = PostData(title,body)
}

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
