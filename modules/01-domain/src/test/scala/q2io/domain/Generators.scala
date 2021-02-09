package q2io.domain

import eu.timepit.refined.api.Refined
import io.estatico.newtype.ops._
import io.estatico.newtype.Coercible
import java.util.UUID
import org.scalacheck._
import domain._

import User._

object Generators {
  implicit def arbCoercibleUUID[A: Coercible[UUID, *]]: Arbitrary[A] =
    Arbitrary(cbUuid[A])

  implicit def arbCoercibleInt[A: Coercible[Int, *]]: Arbitrary[A] =
    Arbitrary(Gen.posNum[Int].map(_.coerce[A]))

  implicit def arbCoercibleStr[A: Coercible[String, *]]: Arbitrary[A] =
    Arbitrary(cbStr[A])

  val genNonEmptyString: Gen[String] =
    Gen
      .chooseNum(21, 40)
      .flatMap { n => Gen.buildableOfN[String, Char](n, Gen.alphaChar) }

  def cbUuid[A: Coercible[UUID, *]]: Gen[A] =
    Gen.uuid.map(_.coerce[A])

  def cbStr[A: Coercible[String, *]]: Gen[A] =
    genNonEmptyString.map(_.coerce[A])

  def cbInt[A: Coercible[Int, *]]: Gen[A] =
    Gen.posNum[Int].map(_.coerce[A])

  val userGen: Gen[User] =
    for {
      uid <- cbUuid[UserId]
      uName <- cbStr[UserName]
    } yield User(uid, uName)

  implicit val arbUser: Arbitrary[User] =
    Arbitrary(userGen)

  val createUserGen: Gen[CreateUser] =
    for {
      uName <- genNonEmptyString
      uPassword <- genNonEmptyString
    } yield CreateUser(username = uName, password = uPassword)

  implicit val arbCreateUser: Arbitrary[CreateUser] =
    Arbitrary(createUserGen)

}
