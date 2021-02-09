package q2io.core
package config

import ciris._
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import scala.concurrent.duration._
import natchez.Trace.Implicits.noop // needed for skunk

object AppConfig {
  @newtype case class PasswordSalt(value: Secret[NonEmptyString])
// AIzaSyBBHBgLQfRjis0WUsSSWeEgpFeLxo-WLYI

  case class AppConfig(
      postgreSQL: PostgreSQLConfig,
      httpClient: HttpClientConfig,
      googlePlaceClient: GooglePlaceClientConfig,
      httpServer: HttpServerConfig,
      passwordSalt: PasswordSalt
  )

  case class PostgreSQLConfig(
      host: NonEmptyString,
      port: UserPortNumber,
      user: NonEmptyString,
      password: NonEmptyString,
      database: NonEmptyString,
      max: PosInt
  )

  case class HttpServerConfig(
      host: NonEmptyString,
      port: UserPortNumber
  )
  case class HttpClientConfig(
      connectTimeout: FiniteDuration,
      requestTimeout: FiniteDuration
  )
  @newtype case class GoogleApiKey(value: Secret[NonEmptyString])
  @newtype case class GooglePlaceURI(value: NonEmptyString)
  case class GooglePlaceClientConfig(
      uri: GooglePlaceURI,
      key: GoogleApiKey
  )
}
