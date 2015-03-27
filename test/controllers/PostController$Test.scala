package controllers


import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

/**
 * Author: @aguestuser
 * Date: 3/26/15
 */

class PostController$Test extends Specification {

  "Post controller" should {

      "show the index page" in new WithApplication {

        lazy val list = route(FakeRequest(GET, "/posts/")).get

        status(list) === OK
        contentType(list) must beSome.which(_ == "text/html")
        contentAsString(list) must contain("Posts")

      }
    }
}
