package services
import javax.inject._

import services.ExercisesConfiguration.Exercises

@Singleton
class ExercisesService @Inject()(exercisesConfiguration: ExercisesConfiguration) {

  def exercises(): Exercises = exercisesConfiguration.configuration()

}
