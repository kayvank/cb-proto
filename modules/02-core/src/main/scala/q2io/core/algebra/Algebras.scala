package q2io.core.algebra
import cats.Parallel
import cats.effect._
import cats.syntax.all._
import skunk._

import q2io.core.algebra.Users._
import q2io.core.algebra.HealthCheck._
import q2io.core.algorithm.Crypto
import q2io.core.config.AppConfig._

object Algebras {

  def apply[F[_]: Concurrent: Parallel: Timer](
      sessionPool: Resource[F, Session[F]],
      cfg: AppConfig
  ): F[Algebras[F]] =
    for {
      crypto <- Crypto(cfg.passwordSalt)
      _users <- Users(sessionPool, crypto)
      _healthCheck <- HealthCheck(sessionPool)
    } yield new Algebras[F](users = _users, healthCheck = _healthCheck)

}

final class Algebras[F[_]] private (
    val users: Users[F],
    val healthCheck: HealthCheck[F]
)
