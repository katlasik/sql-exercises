package services
import javax.inject._
import com.typesafe.config.ConfigFactory
import java.nio.file.Paths
import pureconfig.generic.auto._


@Singleton
class ExercisesConfiguration {

  import ExercisesConfiguration._

  def configuration(): Exercises = exercises match {
    case Right(e) => e
    case Left(e) => throw new IllegalStateException(e.toString)
  }

}

object ExercisesConfiguration {
  case class Exercise(description: String, verifingQuery: String)

  case class Section(name: String, exercises: List[Exercise])

  case class Exercises(sections: List[Section]) {
    def apply(index: Int): Option[Section] = sections.lift(index)
  }

  private final val FilePath = "conf/exercises.conf"

  private lazy val exercises = pureconfig.loadConfigFromFiles[Exercises](List(Paths.get(FilePath)))

}
