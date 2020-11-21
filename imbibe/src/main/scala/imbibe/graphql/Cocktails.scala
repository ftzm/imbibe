package imbibe.graphql

import sangria.schema._
import sangria.execution._
import sangria.marshalling.circe._
import scala.concurrent.ExecutionContext.Implicits.global
import sangria.ast.Document
import scala.concurrent.Future
import io.circe.Json

object Cocktails {
  val QueryType = ObjectType("Query", fields[Unit, Unit](
    Field("cocktails", ListType(StringType), resolve = _ => Seq("example cocktail"))
  ))

  val schema = Schema(QueryType)

  def execute(query: Document): Future[Json] = Executor.execute(schema, query)
}
