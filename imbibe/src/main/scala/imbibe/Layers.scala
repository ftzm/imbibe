package imbibe

import zio._
import zio.blocking.Blocking
import zio.console.Console
import imbibe.persistence._


object Layers {
  val layer0 = zio.console.Console.any ++ zio.blocking.Blocking.any ++ Config.live

  val layer1 = Logging.consoleLogger ++ CocktailPersistence.layer ++ Config.live

  val layers: ZLayer[Console with Blocking, Throwable, logging.Logging with HasCocktailPersistence with config.Config] =
    layer0 >>> layer1
}
