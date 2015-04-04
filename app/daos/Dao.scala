package daos

import anorm.{Row, RowParser, SimpleSql}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.DB
import repos.{Repo, RepoResource}

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */

trait Dao[T,R<:RepoResource[T]] extends Repo[T,R] {

  val parse: RowParser[R]

  def validate(item: T): Option[T]

  def findStatement(id: Long): SimpleSql[Row]
  def findAllStatement: SimpleSql[Row]
  def createStatement(item: T): SimpleSql[Row]
  def editStatement(id: Long, item: T): SimpleSql[Row]
  def deleteStatement(id: Long): SimpleSql[Row]

  def db: String
  def now: DateTime

  def find(id: Long): Option[R] =
    DB.withConnection(db) { implicit c ⇒
      findStatement(id)
        .as(parse.singleOpt) }

  def findAll: List[R] = {
    DB.withConnection(db) { implicit c ⇒
      findAllStatement
        .as(parse.*) } }

  def create(item: T): Option[Long] =
    validate(item) flatMap { i ⇒
      DB.withConnection(db) { implicit c ⇒
        createStatement(item)
          .executeInsert() } }

  def edit(id: Long, item: T): Option[Int] =
    validate(item) flatMap { i ⇒
      optionify { DB.withConnection(db) { implicit c ⇒
        editStatement(id,item)
          .executeUpdate() } } }

  def delete(id: Long): Option[Int] =
    optionify { DB.withConnection(db) { implicit c =>
      deleteStatement(id)
        .executeUpdate() } }

  private def optionify(i: Int): Option[Int] = i match {
    case 0 ⇒ None
    case _ ⇒ Some(1)
  }
}
