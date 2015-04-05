package util

//import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import play.api.{Mode, Play}

/**
 * Author: @aguestuser
 * Date: 4/4/15
 */

object Environment {

  def now: DateTime = if (isTest) s17 else DateTime.now
  def db(default: String): String = if (isTest) "test" else default

  private def isTest: Boolean = Play.current.mode == Mode.Test

  lazy val s17 = new DateTime().withDate(2011,9,17).withTimeAtStartOfDay()
}
