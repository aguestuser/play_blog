package daos

import play.api.{Mode, Play}

/**
 * Author: @aguestuser
 * Date: 3/26/15
 */

trait DbName {
  def dbName = if (Play.current.mode == Mode.Test) "test" else "default"
}

