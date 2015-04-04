package repos

/**
 * Author: @aguestuser
 * Date: 4/3/15
 */

trait Repo[T,R<:RepoResource[T]] {
  def find(id: Long): Option[R]
  def findAll: List[R]
  def create(item: T): Option[Long]
  def edit(id: Long, item: T): Option[Int]
  def delete(id: Long): Option[Int]
}

trait RepoResource[T]
