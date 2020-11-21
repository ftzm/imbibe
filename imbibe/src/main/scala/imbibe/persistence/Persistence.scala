package imbibe.persistence

import zio._
import zio.blocking.Blocking
import zio.interop.catz._

import doobie.{ Query0, Transactor, Update0, Fragments }
import doobie.implicits._

import imbibe._
import cats.data.NonEmptyList

final class CocktailPersistence(xa: Transactor[Task]) extends CocktailPersistence.Service {
  import CocktailPersistence.SQL

  def getAll(): Task[Iterable[Cocktail]] = {
    for {
      rows <- SQL.getAll.to[List].transact(xa).orDie
    } yield rows.groupMap(_._1)(_._2)map{ case (k, v) => Cocktail(k, v)}
  }

  def getByName(name: String): Task[Option[Cocktail]] = {
    for {
      rows <- SQL.getByName(name).to[List].transact(xa).orDie
    } yield rows.groupMap(_._1)(_._2).map{ case (k, v) => Cocktail(k, v)}.headOption
  }

  def getByIngredients(ingredients: NonEmptyList[String]): Task[Iterable[Cocktail]] = {
    for {
      rows <- SQL.getByIngredients(ingredients).to[List].transact(xa).orDie
    } yield rows.groupMap(_._1)(_._2)map{ case (k, v) => Cocktail(k, v)}
  }
}

object CocktailPersistence {
  trait Service {
    def getAll(): Task[Iterable[Cocktail]]
    def getByName(name: String): Task[Option[Cocktail]]
    def getByIngredients(ingredients: NonEmptyList[String]): Task[Iterable[Cocktail]]
  }

  object SQL {

    def getAll(): Query0[Tuple2[String, String]] =
      sql"""
      SELECT c.name, i.name
      FROM cocktails as c
      JOIN ingredient_use as iu
      ON iu.cocktail_id = c.id
      JOIN ingredients as i
      ON i.id = iu.ingredient_id
      """.query[Tuple2[String, String]]

    def getByName(name: String): Query0[Tuple2[String, String]] =
      sql"""
      SELECT c.name, i.name
      FROM cocktails as c
      JOIN ingredient_use as iu
      ON iu.cocktail_id = c.id
      JOIN ingredients as i
      ON i.id = iu.ingredient_id
      WHERE c.name = $name
      """.query[Tuple2[String, String]]

    def getByIngredients(ingredients: NonEmptyList[String]): Query0[Tuple2[String, String]] = {
      val q = fr"""
      SELECT c.name, i.name
      FROM cocktails as c
      JOIN ingredient_use as iu
      ON iu.cocktail_id = c.id
      JOIN ingredients as i
      ON i.id = iu.ingredient_id
      Join (SELECT iu.cocktail_id as id
      FROM ingredient_use as iu
      JOIN ingredients as i
      ON i.id = iu.ingredient_id
      WHERE """ ++ Fragments.in(fr"i.name", ingredients) ++ fr""") as matched_id
      ON c.id = matched_id.id"""
      q.query[Tuple2[String, String]]
    }
  }


  def xa(cfg: DbConfig) = Transactor.fromDriverManager[Task](
    "org.postgresql.Driver", s"jdbc:postgresql://${cfg.host}:${cfg.port}/", cfg.user, cfg.password
  )

  def layer: ZLayer[Blocking with config.Config, Throwable, HasCocktailPersistence] = {
    ZLayer.fromEffect(
      for {
      cfg <- config.getConfig
    } yield new CocktailPersistence(xa(cfg.db))
    )
  }

  def getAll(): RIO[HasCocktailPersistence, Iterable[Cocktail]] =
    ZIO.accessM(_.get.getAll())

  def getByName(name: String): RIO[HasCocktailPersistence, Option[Cocktail]] =
    ZIO.accessM(_.get.getByName(name))

  def getByIngredients(ingredients: NonEmptyList[String]): RIO[HasCocktailPersistence, Iterable[Cocktail]] =
    ZIO.accessM(_.get.getByIngredients(ingredients))
}
