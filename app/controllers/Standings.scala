package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import models._
import views._


object Standings extends Controller {
	//Standing home page (returns template/html)
	def index = Action {
		Ok(html.standings("Standings Home Page"))
	}

	//getAll (returns JSON)
	def getAll = Action {
		Ok(play.api.libs.json.Json.parse(StandingsManager.getAllStandingsJSON().toString()))
	}
}
