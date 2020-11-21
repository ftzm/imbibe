package imbibe.http.graphql

import cats.effect._
import cats.data.NonEmptyList
import org.http4s._
import org.http4s.dsl.io._
import zio.{ ExitCode => ZExitCode, _ }
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import org.http4s.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.optics.JsonPath._
import io.circe.Json
import imbibe.persistence._
import imbibe.graphql._
import sangria.parser.QueryParser


object GQL {
  def gqlEndpoint[R <: HasCocktailPersistence]() = {

    val dsl = Http4sDsl[RIO[R, *]]
    import dsl._

    HttpRoutes.of[RIO[R, *]] {
      case req @ POST -> Root => for {
        body <- req.as[Json]

        res <- root.query.string.getOption(body) match {
          case Some(q) => for {
            query <- ZIO.fromTry(QueryParser.parse(q))
            res <- ZIO.fromFuture(executionContext => Cocktails.execute(query)): ZIO[Any, Throwable, Json]
            resp <- Ok(res)
          } yield resp
          case None => UnprocessableEntity()

        }
      } yield res
    }
  }
}
