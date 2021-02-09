package q2io.bootstrap
package http

import cats.effect._
import cats.syntax.all._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware._
import scala.concurrent.duration._
import q2io.core.algebra.Algebras
import q2io.bootstrap.http.routes._
import q2io.core.algebra.HealthCheck

final case class HttpApi[F[_]: Concurrent: Timer] private (
    algebra: Algebras[F]
) {
  private val userRoutes: HttpRoutes[F] =
    new UserRoutes[F](algebra.users).routes
  private val healthRoutes: HttpRoutes[F] =
    new HealthRoutes[F](algebra.healthCheck).routes

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] => RequestLogger.httpApp(true, true)(http) } andThen {
      http: HttpApp[F] => ResponseLogger.httpApp(true, true)(http)
    }
  }
  val publicRoutes: HttpRoutes[F] =
    userRoutes <+> healthRoutes

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http, CORS.DefaultCORSConfig)
    } andThen { http: HttpRoutes[F] => Timeout(60.seconds)(http) }
  }

  val httpApp: HttpApp[F] = loggers(middleware(publicRoutes).orNotFound)
}

object HttpApi {
  def apply[F[_]: Concurrent: Timer](
      algebras: Algebras[F]
  ): F[HttpApi[F]] = Sync[F].delay(
    new HttpApi[F](
      algebras
    )
  )
}
