package util

import com.github.nscala_time.time.Imports._
import play.api.libs.json.{JsString, JsValue, Writes}

/**
 * Author: @aguestuser
 * Date: 4/4/15
 */

object Date {
  def pp(date: DateTime): String = DateTimeFormat.forPattern("M.d.y @ h:mmaa").print(date).toLowerCase

  implicit val ppDateWrites: Writes[DateTime] = new Writes[DateTime] {
    def writes(d: DateTime): JsValue = JsString(pp(d))
  }
}
