package controllers

import models.PostDao
import play.api.mvc.{Action, Controller}

/**
 * Author: @aguestuser
 * Date: 3/21/15
 */

object Post extends Controller {
  def find(id: Long) = Action { implicit req =>
    PostDao.find(id) match {
      case None => NotFound
      case Some(p) => Ok(views.html.show_post(p))
    }
  }

  def list() = Action { implicit req =>
    PostDao.list match {
      case Nil => NotFound
      case ps => Ok(views.html.list_posts(ps))
    }
  }
}
