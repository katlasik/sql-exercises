package controllers

import javax.inject._
import model.{Failed, Succeded, Unexpected}
import play.api.libs.json.Json
import play.api.mvc._
import services._

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               sqlService: SqlService,
                               exercisesService: ExercisesService)
    extends AbstractController(cc) {

  lazy val exercises = exercisesService.exercises()

  lazy val Schema = Json.toJson(sqlService.schema()).toString()

  def index(idx: Int) = Action { implicit request =>
    Ok(views.html.index(exercises, idx, Schema))
  }

  def sql() = Action { implicit request =>
    {

      val result = for {
        form <- request.body.asFormUrlEncoded
        sqlParams <- form.get("sql")
        sql <- sqlParams.headOption
        verifingQueryParams <- form.get("verifingQuery")
        verifingQuery <- verifingQueryParams.headOption
      } yield (sql, verifingQuery)

      result match {
        case Some((sql, query)) => {
          sqlService.sql(sql, query) match {
            case s: Succeded => Ok(views.html.result(s))
            case Failed(msg) => NotAcceptable(views.html.error(msg))
            case u: Unexpected => NotAcceptable(views.html.unexpected(u))
          }

        }
        case None => BadRequest("Missing sql parameter")
      }
    }
  }
}
