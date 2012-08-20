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
		return ok(index.render("welcome to the adiabats manager" ));
	}

	public static Result jedisTest() {
		Jedis jedis = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
		jedis.incr("foo");
		return ok(index.render("foo:" + jedis.get("foo")));
	}

	public static Result managerHome() {
		/*
		//FIXME temporarily disabled the actual session/auth		
		ApiSession session = (ApiSession)play.cache.Cache.get(sessionKey(session("SFDCUserId")));
		if (session == null) return badRequest("not logged in");	
		String token = session.getAccessToken();
		ForceApi api = new ForceApi(session);
		*/
		ApiConfig config = new ApiConfig()
				.setUsername(System.getenv("PUBLIC_SFDC_USERNAME"))
				.setPassword(System.getenv("PUBLIC_SFDC_PASSWORD"))
				.setClientId(System.getenv("CLIENT_ID"))
				.setClientSecret(System.getenv("CLIENT_SECRET")) ;
		ApiSession session = Auth.authenticate(config);
		ForceApi api = new ForceApi(config, session);
		QueryResult<Player> results = 	
			api.query("select id, name, Nights_Available__c, Positions__c from Player__c where Willing_to_Substitute__c = true", Player.class);
		String response = "welcome to the adiabats manager with this many Players: " + results.getTotalSize();
		response += "\n first name" + results.getRecords().get(0).getName();
	        PlayerSearcher ps = new PlayerSearcher(results.getRecords());	
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
		Logger.warn("using code: " + token);
		ApiConfig config = new ApiConfig()
					.setClientId(System.getenv("CLIENT_ID"))
					.setRedirectURI("https://" + System.getenv("DOMAIN") + "/oauth")
					.setClientSecret(System.getenv("CLIENT_SECRET"));
		ApiSession session = Auth.completeOAuthWebServerFlow(new AuthorizationResponse()
				.apiConfig(config)
				.code(token));
		Logger.warn("about to create ForceApi");	
		ForceApi api = new ForceApi(config,session);
		String userId = api.getIdentity().getUserId();
		play.cache.Cache.set(sessionKey(userId), session);		
		session("SFDCUserId", userId);
		return redirect("/manager/home");
	}

	private static String sessionKey(String userId){
		return "user:"+userId+":session";
	}

/*
	private static String queryFDC() {
		String s = "";
		QueryResult<Map> result = connectToFDC().query("select id, name from Team__c");
		for (Map<?,?> m : result.getRecords()){
			s += "Set: " + m.keySet();
		}
		//s += "max batch size: " + connectToFDC().describeGlobal().getMaxBatchSize();
		//	s += "session: " + connectToFDC().session.getAccessToken();
		//	s += "ID;: " + connectToFDC().getIdentity().getUserId();
		return s;
	}

	public static ForceApi connectToFDC() {
		ForceApi api = new ForceApi(new ApiConfig()
				.setUsername("adiabatsadmin@demo92.com")
				.setPassword("adiabats1232JcvPE55yyuxYa8alQ30udmc")
				.setClientId("3MVG9y6x0357Hlec8S2SO0GslEED6ht6ARorUCD0oJvWAWgBNQThaNgwXJ3esF4iaa3QmY3Zw_LVgaOqEU86c")
				.setClientSecret("3360843589409396938")
				);
		return api;
	}
*/

}
