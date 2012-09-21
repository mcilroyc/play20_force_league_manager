package controllers;

import play.*;
import play.mvc.*;
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

	//handle the call back to this app, with the auth code
	public static Result handleOAuth(String token) {
		//Build the conifg object
		ApiConfig config = new ApiConfig()
			.setClientId(System.getenv("CLIENT_ID"))
			.setRedirectURI("https://" + System.getenv("DOMAIN") + "/oauth")
			.setClientSecret(System.getenv("CLIENT_SECRET"));
		//Use the Token to complete the OAuth process
		ApiSession session = Auth.completeOAuthWebServerFlow(new AuthorizationResponse()
				.apiConfig(config)
				.code(token));
		Logger.debug("auth token: " + session.getAccessToken());
		//create a new Force API instance
		ForceApi api = new ForceApi(config,session);
		//get the SFDC user ID from the API
		String userId = api.getIdentity().getUserId();
		//Put the Force Session in the cache
		play.cache.Cache.set(AuthHelper.sessionKey(userId), session);		
		//Put the user ID in the Play Session
		session(AuthHelper.SFDC_USER_ID_KEY, userId);
		return redirect("/manager/home");
	}
}
