package q2io.bootstrap

import cats.effect._
import cats.syntax.all._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.server.blaze.BlazeServerBuilder
import scala.concurrent.ExecutionContext
import q2io.core.config.AppConfig
import q2io.core.config.Load
import q2io.core.config.AppResources
import q2io.core.http.client.HttpClients
import q2io.core.algebra.Algebras
import q2io.bootstrap.http.HttpApi
import q2io.bootstrap.http.HttpApi._

object Main extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    Load[IO].flatMap { cfg =>
      Logger[IO].info(s"Loaded config $cfg") >>
        AppResources[IO](cfg).use { res =>
          for {
            clients <- HttpClients(cfg.googlePlaceClient, res.httpClient)
            algebra <- Algebras[IO](res.psql, cfg)
            api <- HttpApi[IO](algebra)
            _ <- BlazeServerBuilder[IO](ExecutionContext.global)
              .bindHttp(
                9000,
                "0.0.0.0"
              )
              .withHttpApp(api.httpApp)
              .serve
              .compile
              .drain
          } yield ExitCode.Success

        }

    }
}
