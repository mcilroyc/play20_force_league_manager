package utilities;

import play.*;
import play.cache.*;
import play.mvc.*;
import com.force.api.*;

public class AuthHelper {

	//session key
	public static final String SFDC_USER_ID_KEY = "SFDCUserId";

	public static ForceApi getPrivateForceApiFromSession(String sfdcUserId) {
		if (sfdcUserId == null) return null;
		ApiSession session;
		try {
			session = (ApiSession)play.cache.Cache.get(sessionKey(sfdcUserId));
			if (session == null) {
				return null;
			}
			else {
				Logger.info("using session with auth token: " + session.getAccessToken());
				Logger.info("using session with getApiEndpoint: " + session.getApiEndpoint());
				return new ForceApi(session);
			}
		}
		catch (Exception e) {
			return null;
			//TODO Bad
		}
	}

	public static ForceApi getPrivateForceApiFromToken(String token) {
		ApiConfig config = new ApiConfig()
			.setRedirectURI("https://" + System.getenv("DOMAIN") + "/oauth")
			.setClientId(System.getenv("CLIENT_ID"))
			.setClientSecret(System.getenv("CLIENT_SECRET")) ;
		ApiSession session = new ApiSession()
			.setAccessToken(token)
			.setApiEndpoint("https://na9.salesforce.com");
		try {
			return new ForceApi(config,session);
		}
		catch (Exception e) {
			return null;
			//TODO Bad
		}
	}

	public static CustomForceApi getPublicCustomApi() { 
		ApiConfig config = new ApiConfig()
			.setUsername(System.getenv("PUBLIC_SFDC_USERNAME"))
			.setPassword(System.getenv("PUBLIC_SFDC_PASSWORD"))
			.setClientId(System.getenv("CLIENT_ID"))
			.setClientSecret(System.getenv("CLIENT_SECRET")) ;
		ApiSession session = Auth.authenticate(config);
		return new CustomForceApi(config,session);
	}

	public static String sessionKey(String userId){
		return "user:"+userId+":session";
	}

}
