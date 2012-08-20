package controllers;

import play.*;
import play.mvc.*;
import models.CustomForceApi;

import views.html.*;
import com.force.api.*;
import java.util.Map;
import play.cache.Cache;
import org.codehaus.jackson.JsonNode;
import static play.libs.Json.parse;

public class Standings extends Controller {

	static final String STANDINGS_KEY = "standings";

	public static Result index() {
		String standingsJson = null;
		standingsJson = (String)play.cache.Cache.get(STANDINGS_KEY);
		if (standingsJson != null){
			Logger.debug("standings pulled from cache");
			return ok(parse(standingsJson));
		}
		else {
			JsonNode standingsJsonNode = fetchStandingsFromSFDC();
			if (standingsJsonNode != null){
				Logger.debug("standings fetched, adding to cache");
				play.cache.Cache.set(STANDINGS_KEY,standingsJsonNode.toString(),120);
				return (ok(standingsJsonNode));
			}
			else {
				return notFound();
			}
		}
	}

	private static JsonNode fetchStandingsFromSFDC(){
		ApiConfig config = new ApiConfig()
				.setUsername(System.getenv("PUBLIC_SFDC_USERNAME"))
				.setPassword(System.getenv("PUBLIC_SFDC_PASSWORD"))
				.setClientId(System.getenv("CLIENT_ID"))
				.setClientSecret(System.getenv("CLIENT_SECRET")) ;
		ApiSession session = Auth.authenticate(config);
		CustomForceApi api = new CustomForceApi(config,session);
		return api.getAsJsonNode("standings/current");
	}
}
