package q2io.core
package config

import cats.effect._
import cats.syntax.all._
import ciris._
import ciris.refined._
import Environments._
import Environments.AppEnvironment._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import scala.concurrent.duration._
import AppConfig._

import cats.effect.ContextShift

object Load {
  def apply[F[_]: Async: ContextShift]: F[AppConfig] = {
    env("APP_ENV")
      .as[AppEnvironment]
      .flatMap {
        case Test | Dev | Prod =>
          default() //TODO parms = unchangeable attributes
      }
      .load[F]
  }
  def default(): ConfigValue[AppConfig] =
    (
      env("GOOGLE_API_KEY").as[NonEmptyString].secret,
      env("PASSWORD_SALT").as[NonEmptyString].secret,
      env("POSTGRES_HOST").as[NonEmptyString],
      env("POSTGRES_USER_NAME").as[NonEmptyString],
      env("POSTGRES_PASSWORD").as[NonEmptyString], // TODO use secret
      env("POSTGRES_DATABASE").as[NonEmptyString]
    ).parMapN {
      (
          googleApiKey,
          _passwordSalt,
          pgHost,
          pgUserName,
          pgPassword,
          pgDatabase
      ) =>
        AppConfig(
          PostgreSQLConfig(
            host = pgHost,
            port = 5432,
            user = pgUserName,
            password = pgPassword,
            database = pgDatabase,
            max = 10
          ),
          HttpClientConfig(
            connectTimeout = 2.seconds,
            requestTimeout = 2.seconds
          ),
          GooglePlaceClientConfig(
            uri = GooglePlaceURI(
              "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
            ),
            key = GoogleApiKey(googleApiKey)
          ),
          HttpServerConfig(
            host = "0.0.0.0",
            port = 9000
          ),
          passwordSalt = PasswordSalt(_passwordSalt)
        )
    }

}
