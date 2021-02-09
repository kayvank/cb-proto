package q2io.domain

import io.estatico.newtype.macros.newtype

import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined._
import eu.timepit.refined.types.string.NonEmptyString
import java.util.UUID
import javax.crypto.Cipher
import scala.util.control.NoStackTrace
import ciris.refined._
import ciris._

object User {

  case class User(id: UserId, name: UserName)
  @newtype case class UserId(value: UUID)
  @newtype case class UserName(value: String)
  @newtype case class Password(value: String)
  @newtype case class UserNameParam(value: NonEmptyString) {
    def toDomain: UserName = UserName(value.value.toLowerCase)
  }
  @newtype case class EncryptedPassword(value: String)
  @newtype case class PasswordParam(value: NonEmptyString) {
    def toDomain: Password = Password(value.value)
  }
  @newtype case class EncryptCipher(value: Cipher)
  @newtype case class DecryptCipher(value: Cipher)

  case class CreateUser(
      username: String,
      password: String
  )

  case class LoginUser(
      username: UserNameParam,
      password: PasswordParam
  )

  case class InvalidUserOrPassword(username: UserName) extends NoStackTrace
  case class UserNameInUse(username: UserName) extends NoStackTrace

  case object UnsupportedOperation extends NoStackTrace
}
