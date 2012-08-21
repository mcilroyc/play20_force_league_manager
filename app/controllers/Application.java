package controllers;

import play.*;
import play.mvc.*;
import models.*;

import views.html.*;
import com.force.api.*;
import java.util.Map;

import static play.libs.Json.toJson;
import com.typesafe.plugin.RedisPlugin;
import redis.clients.jedis.*;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render("welcome to the DF12 manager" ));
	}

	public static Result jedisTest() {
		Jedis jedis = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		jedis.incr("foo");
		return ok(index.render("foo:" + jedis.get("foo")));
	}

	public static Result managerHome() {
		//get the authentication from the session
		ApiSession session = (ApiSession)play.cache.Cache.get(sessionKey(session("SFDCUserId")));
		if (session == null) return badRequest("not logged in");	
		String token = session.getAccessToken();
		ForceApi api = new ForceApi(session);

		//Query SFDC
		QueryResult<Player> results = 	
			api.query("select id, name, Nights_Available__c, Positions__c from Player__c where Willing_to_Substitute__c = true", Player.class);
		String response = "welcome to the adiabats manager with this many Players: " + results.getTotalSize();
		response += "\n first name" + results.getRecords().get(0).getName();
	        PlayerSearcher ps = new PlayerSearcher(results.getRecords());	

		//render the response
		return ok(index.render(response ));
	}

	public static Result managerTest() {
		return ok(toJson(PlayerSearcher.getPitchersOnWednesday()));
	}

	public static Result loginToSFDC() {
		String url = Auth.startOAuthWebServerFlow(new AuthorizationRequest()
				.apiConfig(new ApiConfig()
				.setClientId(System.getenv("CLIENT_ID"))
				.setRedirectURI("https://" + System.getenv("DOMAIN") + "/oauth"))
				.state("mystate"));
		return redirect(url);
	}

	public static Result handleOAuth(String token) {
		Logger.debug("using code: " + token);
		ApiConfig config = new ApiConfig()
					.setClientId(System.getenv("CLIENT_ID"))
					.setRedirectURI("https://" + System.getenv("DOMAIN") + "/oauth")
					.setClientSecret(System.getenv("CLIENT_SECRET"));
		ApiSession session = Auth.completeOAuthWebServerFlow(new AuthorizationResponse()
				.apiConfig(config)
				.code(token));
		ForceApi api = new ForceApi(config,session);
		String userId = api.getIdentity().getUserId();
		play.cache.Cache.set(sessionKey(userId), session);		
		session("SFDCUserId", userId);
		return redirect("/manager/home");
	}

	private static String sessionKey(String userId){
		return "user:"+userId+":session";
	}

}
