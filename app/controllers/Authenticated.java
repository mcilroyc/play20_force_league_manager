package controllers;

import play.mvc.Action.Simple;
import play.mvc.Http.Context;
import play.mvc.*;
import com.force.api.*;
import utilities.*;

public class Authenticated extends Simple {

	@Override
		public Result call(Context ctx) throws Throwable {
			ForceApi api = AuthHelper.getPrivateForceApiFromSession(ctx.session().get(AuthHelper.SFDC_USER_ID_KEY));
			if(api != null) {
				ctx.args.put("api",api);
				return delegate.call(ctx);
			} else {
				return redirect(routes.AuthController.loginToSFDC());
			}
		}
}
