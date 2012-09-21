package controllers;

import play.mvc.Action.Simple;
import play.mvc.Http.Context;
import play.mvc.*;
import com.force.api.*;
import utilities.*;

public class Authenticated extends Simple {

	/*
	 * Try to get the force.com API session out of cache
	 * If it's not there, try to the backdoor (authToken in the querystring)
	 * Otherwise redirect to the login page 
	*/
	@Override
	public Result call(Context ctx) throws Throwable {
		ForceApi api = AuthHelper.getPrivateForceApiFromSession(ctx.session().get(AuthHelper.SFDC_USER_ID_KEY));
		if(api != null) {
			ctx.args.put("api",api);
			return delegate.call(ctx);
		} else {
			String[] authToken = ctx.request().queryString().get("authToken");
			if (authToken != null && authToken.length > 0) {
				api = AuthHelper.getPrivateForceApiFromToken(authToken[0]);	
				ctx.args.put("api",api);
			}
			if (api != null) {
				return delegate.call(ctx);
			}
			else {
				return redirect(routes.AuthController.loginToSFDC());
			}
		}
	}
}
