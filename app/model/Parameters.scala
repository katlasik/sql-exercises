package model

sealed trait Parameters

case class Plain(parameters: List[String]) extends Parameters

case class Named(parameters: Map[String, String]) extends Parameters

object Parameters {
  val Empty = Plain(Nil)
}
