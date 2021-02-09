package q2io.core
package http
package client

import cats.syntax.all._
import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.Method._

import q2io.core.config.AppConfig._
import q2io.domain.effects._
import cats.data.NonEmptyList
import eu.timepit.refined.types.string.NonEmptyString
import scala.util.control.NoStackTrace

case class GooglePlaceClientError(cause: String) extends NoStackTrace
case class LocationRequest(
    lat: Double,
    lng: Double,
    radius: Int = 1500,
    placeType: String = "cruise"
)
case class LocationResponse(value: String)

trait GooglePlaceClient[F[_]] {
  def place(request: LocationRequest): F[String]
  def findPlace(uri: String): F[String]
  def placeUri(request: LocationRequest): String
}

final class LiveGooglePlaceClient[F[_]: JsonDecoder: BracketThrow](
    cfg: GooglePlaceClientConfig,
    client: Client[F]
) extends GooglePlaceClient[F]
    with Http4sClientDsl[F] {

  override def place(location: LocationRequest): F[String] =
    (placeUri _ andThen findPlace)(location)

  override def placeUri(location: LocationRequest): String =
    s"""
        |${cfg.uri.value.value}
        |?location=${location.lat},${location.lng}
        |&radius=${location.radius}
        |&type=${location.placeType}
        |&keyword=cruise
        |&key=${cfg.key.value.value.value}
    """.stripMargin

  override def findPlace(uri: String): F[String] = {
    Uri.fromString(uri).liftTo[F].flatMap { _uri =>
      GET(_uri).flatMap { req =>
        client.run(req).use { r =>
          if (r.status == Status.Ok)
            r.asJsonDecode[String]
          else
            GooglePlaceClientError(
              Option(r.status.reason).getOrElse("Unkown")
            ).raiseError[F, String]
        }
      }
    }
  }

}
