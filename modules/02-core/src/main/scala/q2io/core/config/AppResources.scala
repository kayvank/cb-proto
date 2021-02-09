package q2io.core
package config

import cats.effect._
import cats.syntax.all._
import scala.concurrent.ExecutionContext
import io.chrisdavenport.log4cats.Logger
import skunk._
import org.http4s.client.Client
import java.net.http.HttpClient
import org.http4s.client.blaze.BlazeClient
import org.http4s.client.blaze.BlazeClientBuilder
import natchez.Trace.Implicits.noop

import config.AppConfig._

final case class AppResources[F[_]](
    psql: Resource[F, Session[F]],
    httpClient: Client[F]
)
object AppResources {
  def apply[F[_]: ConcurrentEffect: ContextShift: Logger](
      cfg: AppConfig
  ): Resource[F, AppResources[F]] = {
    val makePostgreSqlResource: PostgreSQLConfig => SessionPool[F] = pgSqlCfg =>
      Session.pooled[F](
        host = pgSqlCfg.host.value,
        port = pgSqlCfg.port.value,
        user = pgSqlCfg.user.value,
        password = Some(pgSqlCfg.password.value),
        database = pgSqlCfg.database.value,
        max = pgSqlCfg.max.value
      )
    val makeHttpClient: HttpClientConfig => Resource[F, Client[F]] =
      clientCfg =>
        BlazeClientBuilder[F](ExecutionContext.global)
          .withConnectTimeout(clientCfg.connectTimeout)
          .withRequestTimeout(clientCfg.requestTimeout)
          .resource

    (
      makePostgreSqlResource(cfg.postgreSQL),
      makeHttpClient(cfg.httpClient)
    ).mapN(AppResources.apply[F])
  }
}
