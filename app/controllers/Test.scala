package controllers

object Test extends App{

  import scala.language.higherKinds

  trait TBase[A,B]

  class TTBase[A,B] extends TBase[String,Int]

  trait ConsumerRecord[A,B]

  def recordToType[T, F, A[_, _] <: TBase[T, F]](record: ConsumerRecord[String, Array[Byte]])(
    implicit m: scala.reflect.ClassTag[A[_,_]]
  ): A[T,F] = {
    val a: A[T, F] = m.runtimeClass.getConstructors.head.newInstance().asInstanceOf[A[T, F]]
    a
  }

  val result = recordToType[String, Int, TTBase](null)

  println(result)

}
