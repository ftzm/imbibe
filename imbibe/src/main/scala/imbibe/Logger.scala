package imbibe

import zio._

package object logging {
  type Logging = Has[Logging.Service]
}

object Logging {
  // This is just to get a feel for effects, not my real approach to logging.
  trait Service {
    def info(s: String): UIO[Unit]
    def error(s: String): UIO[Unit]
  }

  import zio.console.Console
  val consoleLogger: ZLayer[Console, Nothing, logging.Logging] = ZLayer.fromFunction( console =>
    new Service {
      def info(s: String): UIO[Unit]  = console.get.putStrLn(s"info - $s")
      def error(s: String): UIO[Unit] = console.get.putStrLn(s"error - $s")
    }
  )

  //accessor methods
  def info(s: String): URIO[logging.Logging, Unit] =
    ZIO.accessM(_.get.info(s))

  def error(s: String): URIO[logging.Logging, Unit] =
    ZIO.accessM(_.get.error(s))
}
