package q2io.core.algebra

import cats.Parallel
import cats.effect._
import cats.effect.implicits._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import scala.concurrent.duration._

import q2io.domain.HealthCheck._

trait HealthCheck[F[_]] {
  def status: F[AppStatus]
}

object HealthCheck {
  def apply[F[_]: Concurrent: Parallel: Timer](
      sessionPool: Resource[F, Session[F]]
  ): F[HealthCheck[F]] =
    Sync[F].delay(
      new HealthCheck[F] {

        val q: Query[Void, Int] =
          sql"SELECT pid FROM pg_stat_activity".query(int4)

        val postgresHealth: F[PostgresStatus] =
          sessionPool
            .use(_.execute(q))
            .map(_.nonEmpty)
            .timeout(1.second)
            .orElse(false.pure[F])
            .map(PostgresStatus.apply)

        val status: F[AppStatus] =
          (postgresHealth).map(AppStatus)

      }
    )
}
