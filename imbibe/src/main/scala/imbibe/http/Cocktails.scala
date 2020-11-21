package imbibe.http.cocktails

import cats.effect._
import cats.data.NonEmptyList
import org.http4s._
import org.http4s.dsl.io._
import scala.concurrent.ExecutionContext.Implicits.global
import zio.{ ExitCode => ZExitCode, _ }
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.clock.Clock

import org.http4s.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import imbibe.persistence._

object Cocktails {
  def cocktailsService[R <: HasCocktailPersistence]() = {

    case class IngredientsQuery(ingredients: NonEmptyList[String])

    implicit val decoder = jsonOf[RIO[R, *], IngredientsQuery]

    val dsl = Http4sDsl[RIO[R, *]]
    import dsl._

    HttpRoutes.of[RIO[R, *]] {
      case GET -> Root / "cocktails" => {
        CocktailPersistence.getAll().flatMap(cs => Ok(cs.asJson))
      }
      case GET -> Root / "cocktails" / name => {
        CocktailPersistence.getByName(name).flatMap{
          case Some(c) => Ok(c.asJson)
          case None => NotFound()
        }
      }
      case req @ POST -> Root / "ingredients" => for {
        q <- req.as[IngredientsQuery]
        results <- CocktailPersistence.getByIngredients(q.ingredients)
        res <- Ok(results.asJson)
      } yield res
    }
  }
}
