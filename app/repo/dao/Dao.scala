package repo.dao

import anorm._
import play.api.db.DB
import repo.{Repo, RepoResource}

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */

trait Dao[T,R<:RepoResource[T]] extends Repo[T,R] with DbName {

  def validate(item: T): Option[T]

  val table_name: String
  val sql_row: RowParser[R]
  val create_statement: SimpleSql[Row]
  val edit_statement: SimpleSql[Row]

  def find(id: Long): Option[R] =
    DB.withConnection(dbName) { implicit c ⇒
      SQL"select * from $table_name where id = $id"
        .as(sql_row.singleOpt) }

  def findAll: List[R] = {
    DB.withConnection(dbName) { implicit c ⇒
      SQL"select * from $table_name"
        .as(sql_row.*) } }

  def create(item: T): Option[Long] =
    validate(item) flatMap { i ⇒ create_statement.executeInsert() }

  def edit(id: Long, item: T): Option[Int] =
    validate(item) flatMap { i ⇒
      optionify { DB.withConnection(dbName) { implicit c ⇒
        edit_statement.executeUpdate() } } }

  def delete(id: Long): Option[Int] =
    optionify { DB.withConnection(dbName) { implicit c =>
      SQL"delete from $table_name where id = $id"
        .executeUpdate() } }

  private def optionify(i: Int): Option[Int] = i match {
    case 0 ⇒ None
    case _ ⇒ Some(1)
  }
}
