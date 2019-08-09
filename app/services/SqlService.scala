package services

import java.sql.{ResultSet, SQLSyntaxErrorException}

import javax.inject.{Inject, Singleton}
import model.Result.{Headers, Results}
import model.{Failed, Parameters, Result, Succeded, Unexpected}
import play.api.db._

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

@Singleton
class SqlService @Inject()(db: Database, exercisesService: ExercisesService) {

  val Schema = "school"

  val Exercises = exercisesService.exercises()

  def processResult(rs: ResultSet): (Headers, Results) = {

    val columns = List(rs.getMetaData).flatMap(m =>
      List.range(1, m.getColumnCount + 1).map(m.getColumnName))

    @tailrec
    def traverse(rs: ResultSet, acc: Results = Nil): Results = {
      if (rs.next()) {
        traverse(
          rs,
          List.range(1, columns.size + 1).map(i => String.valueOf(rs.getObject(i))) :: acc)
      } else {
        acc.reverse
      }
    }

    (columns, traverse(rs))

  }

  def sql(sql: String,
          validatingQueryNumber: String,
          variables: Parameters = Parameters.Empty): Result = {

    val Array(section, exercise) =
      validatingQueryNumber.split("\\.").map(_.toInt)

    val validatingQuery = Exercises(section)
      .flatMap(_.exercises.lift(exercise))
      .map(_.verifingQuery) match {
      case Some(q) => q
      case None => throw new IllegalStateException()
    }

    db.withConnection { connection =>
      val statement = connection.createStatement()

      Try(statement.executeQuery(sql)) match {
        case Success(result) =>
          processResult(result) match {
            case (headers, columns) => {
              processResult(statement.executeQuery(validatingQuery)) match {
                case (expectedHeaders, expectedColums) =>
                  if (columns == expectedColums) {
                    Succeded(headers, columns)
                  } else {
                    Unexpected(headers,
                      columns,
                      expectedHeaders,
                      expectedColums)
                  }
              }
            }
          }
        case Failure(e: SQLSyntaxErrorException) =>
          Failed(e.getLocalizedMessage)
        case Failure(_) =>
          Failed("Nie mogę przetworzyć zapytania.")

      }

    }

  }

  def schema(): Map[String, List[String]] = {

    db.withConnection(
      connection =>
        processResult(
          connection
            .createStatement()
            .executeQuery(
              s"""SELECT TABLE_NAME, COLUMN_NAME
                 |FROM INFORMATION_SCHEMA.COLUMNS
                 |WHERE table_schema = '$Schema'
                 |ORDER BY TABLE_NAME""".stripMargin
            )) match {
          case (_, schema) =>
            schema
              .groupBy {
                case List(table: String, _) => table
              }
              .view
              .mapValues(_.map {
                case List(_, column: String) => column
              })
              .toMap
        })

  }

}
