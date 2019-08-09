package model

import model.Result.{Headers, Results}

sealed trait Result

sealed  trait Received extends Result

object Result {
  type Headers = List[String]

  type Results = List[List[String]]
}

case class Succeded(headers: Headers, results: Results) extends Received

case class Unexpected(headers: Headers,
                      results: Results,
                      expectedHeaders: Headers,
                      expectedResults: Results)
    extends Received

case class Failed(msg: String) extends Result
