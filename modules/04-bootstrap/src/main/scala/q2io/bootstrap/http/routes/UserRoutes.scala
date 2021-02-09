package q2io.bootstrap
package http
package routes

import cats._
import cats.syntax.all._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server._
import q2io.domain.effects._
import q2io.domain.Protocol._
import q2io.core.algorithm.Crypto
import q2io.core.algebra.Users._
import q2io.core.algebra._
import q2io.domain.User._
import q2io.domain.Protocol._
import q2io.domain.Protocol.{createUserEncoder, createUserDecoder}

final class UserRoutes[F[_]: Defer: JsonDecoder: MonadThrow](
    usersRepo: Users[F]
) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/v1"
  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "users" =>
      req.asJsonDecode[CreateUser].flatMap { user =>
        usersRepo
          .create(
            UserName(user.username),
            Password(user.password)
          )
          .flatMap(x => Created(x.asJson))
          .recoverWith {
            case UserNameInUse(user) => Conflict(user.value)
          }
      }
    case GET -> Root / "users" / username =>
      Ok(usersRepo.find(UserName(username)))
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
