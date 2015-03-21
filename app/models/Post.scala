package models

/**
 * Author: @aguestuser
 * Date: 3/21/15
 */

case class Post(id: Long, title: String, body: String)

trait PostRepo {
  def find(id: Long): Option[Post]
  def list: List[Post]
  def create(title: String, body: String): Option[Post]
  def update(id: Long, edits: Post): Option[Post]
  def delete(id: Long): Int
}

object PostDao extends PostRepo {

  import anorm._
  import anorm.SqlParser._
  import play.api.db.DB
  import play.api.Play.current

  val post = {
    get[Long]("id") ~ get[String]("title") ~ get[String]("body") map {
    case i ~ t ~ b => Post(i,t,b) } }

  def find(p: Post): Option[Post] = find(p.id)
  def find(id: Long): Option[Post] = DB.withConnection { implicit c =>
   SQL("select * from posts where id = {id}")
    .on("id" -> id) match {
     case Seq() => None
     case p => Some(p.as(post *).head) } }

  def list: List[Post] = DB.withConnection { implicit c =>
    SQL("select * from posts").as(post *) } // TODO add "order by created asc"

  def create(title: String, body: String): Option[Post] =
    if (title.length < 3 || body.length < 3) None
    else DB.withConnection { implicit c =>
      val id: Option[Long] = SQL("insert into posts (title,body) values ({t,b})")
        .on("t" -> title, "b" -> body)
        .executeInsert()
      id flatMap { id => find(id) } }

  def update(p: Post, editedPost: Post): Option[Post] = update(p.id, editedPost)
  def update(id: Long, ep: Post): Option[Post] = DB.withConnection { implicit c =>
    SQL("update posts set title = {t}, body = {b} where id = {id}")
      .on("t" -> ep.title, "b" -> ep.body)
      .executeUpdate() } match {
    case 0 => None
    case _ => find(id) }

  def delete(p: Post): Int = delete(p.id)
  def delete(id: Long): Int = DB.withConnection { implicit c =>
    SQL("delete from posts where id = {id}").on("id" -> id).executeUpdate() }

}

trait PostMock extends PostRepo { ??? }

class PostService(pr: PostRepo) { ??? }
