package controllers;

import authentication.JWTAuthenticator;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(value = JWTAuthenticator.class)
public class SensorController extends Controller {

    public Result getSensorState() {
        return ok();
    }

}
