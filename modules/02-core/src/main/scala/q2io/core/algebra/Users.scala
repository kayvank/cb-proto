package q2io.core
package algebra

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import ext.skunkx._

import q2io.domain.User._
import q2io.core.algorithm.Crypto
import q2io.domain.effects._

trait Users[F[_]] {
  def find(username: UserName): F[Option[User]]
  def create(username: UserName, password: Password): F[UserId]
}

object Users {
  def apply[F[_]: Sync: BracketThrow: GenUUID](
      sessionPool: Resource[F, Session[F]],
      crypto: Crypto
  ): F[Users[F]] = {
    import UserQueries._
    Sync[F].delay(
      new Users[F] {
        override def find(
            username: UserName
        ): F[Option[User]] = sessionPool.use { session =>
          session.prepare(selectUser).use { q =>
            q.option(username).map {
              case Some(u ~ p) =>
                u.some
              case _ => none[User]
            }
          }
        }

        override def create(username: UserName, password: Password): F[UserId] =
          sessionPool.use {
            session =>
              session.prepare(insertUser).use { cmd =>
                GenUUID[F].make[UserId].flatMap { id =>
                  cmd
                    .execute(User(id, username) ~ crypto.encrypt(password))
                    .as(id)
                    .handleErrorWith {
                      case SqlState.UniqueViolation(_) =>
                        UserNameInUse(username).raiseError[F, UserId]
                    }
                }
              }
          }
      }
    )
  }
}

private object UserQueries {

  val codec: Codec[User ~ EncryptedPassword] =
    (uuid.cimap[UserId] ~ varchar.cimap[UserName] ~ varchar
      .cimap[EncryptedPassword]).imap {
      case i ~ n ~ p =>
        User(i, n) ~ p
    } {
      case u ~ p =>
        u.id ~ u.name ~ p
    }

  val selectUser: Query[UserName, User ~ EncryptedPassword] =
    sql"""
        SELECT * FROM users
        WHERE name = ${varchar.cimap[UserName]}
       """.query(codec)

  val insertUser: Command[User ~ EncryptedPassword] =
    sql"""
        INSERT INTO users
        VALUES ($codec)
        """.command

}
