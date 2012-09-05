package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import models.*;
//import models.CustomForceApi;
//import com.force.api.*;
//import java.util.Map;
//import play.cache.Cache;
//import org.codehaus.jackson.JsonNode;
//import static play.libs.Json.parse;

public class Standings extends Controller {

	//Standing home page (returns template/html)
	public static Result index() {
		return ok(index.render("Standings Home Page"));
	}
	
	//getAll (returns JSON)
	public static Result getAll() {
		return ok(StandingsManager.getAllStandingsJSON());
	}
}
