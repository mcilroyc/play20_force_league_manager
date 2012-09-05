package controllers;

import play.*;
import play.mvc.*;
//import views.html.*;
import com.force.api.*;
import play.Logger;
import play.cache.Cache;
import utilities.AuthHelper;

public class AuthController extends Controller {

	//initiate the login to SFDC
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
		Logger.debug("auth token: " + session.getAccessToken());
		ForceApi api = new ForceApi(config,session);
		String userId = api.getIdentity().getUserId();
		play.cache.Cache.set(AuthHelper.sessionKey(userId), session);		
		session(AuthHelper.SFDC_USER_ID_KEY, userId);
		return redirect("/manager/home");
	}
}
