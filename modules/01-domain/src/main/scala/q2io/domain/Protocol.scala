package q2io.domain

import cats._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

import User._
import HealthCheck._

object Protocol extends JsonProtocol {
  implicit def deriveEntityEncoder[F[_]: Applicative, A: Encoder]
      : EntityEncoder[F, A] = jsonEncoderOf[F, A]
}

trait JsonProtocol {
  // ----- Coercible codecs -----
  implicit def coercibleDecoder[A: Coercible[B, *], B: Decoder]: Decoder[A] =
    Decoder[B].map(_.coerce[A])

  implicit def coercibleEncoder[A: Coercible[B, *], B: Encoder]: Encoder[A] =
    Encoder[B].contramap(_.repr.asInstanceOf[B])

  implicit def coercibleKeyDecoder[A: Coercible[B, *], B: KeyDecoder]
      : KeyDecoder[A] =
    KeyDecoder[B].map(_.coerce[A])

  implicit def coercibleKeyEncoder[A: Coercible[B, *], B: KeyEncoder]
      : KeyEncoder[A] =
    KeyEncoder[B].contramap[A](_.repr.asInstanceOf[B])

  // domain encode/decoders

  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

  implicit val createUserDecoder: Decoder[CreateUser] =
    deriveDecoder[CreateUser]

  implicit val createUserEncoder: Encoder[CreateUser] =
    deriveEncoder[CreateUser]

  implicit val UserNameParamDecoder: Decoder[UserNameParam] =
    Decoder.forProduct1("name")(UserNameParam.apply)

  // implicit val UserId: Encoder[UserId] =
  //   Encoder.forProduct1("userId")(_.value)

  implicit val appStatusEncoder: Encoder[AppStatus] = deriveEncoder[AppStatus]
  // implicit val userIdEncoder: Encoder[UserId] = deriveEncoder[UserId]
}
