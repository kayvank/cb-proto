package q2io.domain

import io.circe._
import io.circe.syntax._
import shapeless._
import Generators._
import org.specs2._
import User._
import Protocol._

class UserSpec extends Specification with ScalaCheck {
  def is = s2"""
    Domain model protocol Json specs
        User json codecs  $e1
        CreateUser json codecs  $e1
"""
  def e1 =
    prop((user: User) => user.asJson.as[User] must beRight)
      .setArbitrary(arbUser)

  def e2 =
    prop((createUser: CreateUser) =>
      createUser.asJson.as[CreateUser] must beRight
    ).setArbitrary(arbCreateUser)
}
