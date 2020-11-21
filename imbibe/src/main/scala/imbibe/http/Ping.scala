package imbibe.http.ping

import org.http4s._
import org.http4s.dsl.io._
import scala.concurrent.ExecutionContext.Implicits.global
import zio._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.clock.Clock

object Ping {

  def helloWorldService[R <: Clock]() = {

    val dsl = Http4sDsl[RIO[R, *]]
    import dsl._

    HttpRoutes.of[RIO[R, *]] {
      case GET -> Root / "ping" => {
        Ok("pong")
      }
    }
  }
}
