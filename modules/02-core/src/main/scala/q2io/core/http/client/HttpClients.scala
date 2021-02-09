package q2io.core
package http
package client

import cats.effect._
import org.http4s.client.Client
import q2io.core.config.AppConfig._

object HttpClients {
  def apply[F[_]: Sync](
      cfg: GooglePlaceClientConfig,
      client: Client[F]
  ): F[HttpClients[F]] =
    Sync[F].delay(
      new HttpClients[F] {
        override def placesNearBy: GooglePlaceClient[F] =
          new LiveGooglePlaceClient[F](cfg, client)
      }
    )
}

trait HttpClients[F[_]] {
  def placesNearBy: GooglePlaceClient[F]
}
