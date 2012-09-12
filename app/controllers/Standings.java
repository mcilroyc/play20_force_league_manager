package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import models.*;

public class Standings extends Controller {

	//Standing home page (returns template/html)
	public static Result index() {
		return ok(standings.render("Standings Home Page"));
	}
	
	//getAll (returns JSON)
	public static Result getAll() {
		return ok(StandingsManager.getAllStandingsJSON());
	}
}
