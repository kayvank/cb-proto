package q2io.domain

import io.estatico.newtype.macros._

object HealthCheck {
  @newtype case class PostgresStatus(value: Boolean)

  case class AppStatus(
      postgres: PostgresStatus
  )
}
