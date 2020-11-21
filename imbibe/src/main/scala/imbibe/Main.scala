package imbibe

import zio.{ ExitCode => ZExitCode, _ }
import zio.console._
import zio.clock.Clock
import zio.blocking.Blocking
import zio.interop.catz._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.Router
import imbibe.http.cocktails.Cocktails
import imbibe.http.ping.Ping
import imbibe.http.graphql.GQL
import ch.qos.logback.classic.{Level,Logger}
import org.slf4j.LoggerFactory
import imbibe.persistence._

case class Cocktail(name:String, ingredients: List[String])

object Imbibe extends App {

  LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.INFO)

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ZExitCode] = {
    val prog =
      for {
        _ <- Logging.info("Starting Application.")
        cfg <- config.getConfig
        _ <- runHttp(cfg.api)
      } yield ZExitCode.success
    prog
      .provideSomeLayer[ZEnv](Layers.layers)
      .orDie
  }

  type AppTask[A] = RIO[Clock with HasCocktailPersistence, A]

  val httpApp: HttpApp[AppTask] = Router[AppTask](
    "/ping" -> Ping.helloWorldService(),
    "/cocktails" -> Cocktails.cocktailsService(),
    "/graphql" -> GQL.gqlEndpoint()
  ).orNotFound

  def runHttp[R <: Clock with HasCocktailPersistence](cfg: ApiConfig): ZIO[R, Throwable, Unit] = {
    ZIO.runtime[R].flatMap { implicit runtime =>
      BlazeServerBuilder[AppTask]
      .bindHttp(cfg.port, cfg.host)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
    }
  }

}
