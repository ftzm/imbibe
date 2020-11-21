package imbibe

import zio._
import pureconfig._
import pureconfig.generic.auto._

case class ApiConfig(host: String, port: Int)
case class DbConfig(host: String, port: Int, user: String, password: String)

object Config {
  case class Config(api: ApiConfig, db: DbConfig)

  val live: ZLayer[Any, Throwable, config.Config] =
    ZLayer.fromEffect {
      ZIO.effectTotal(loadConfigOrThrow[Config])
    }
}
