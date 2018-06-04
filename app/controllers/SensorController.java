package controllers;

import authentication.JWTAuthenticator;
import filters.SensorActivityFilter;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(value = JWTAuthenticator.class)
public class SensorController extends Controller {

    private static final long SENSOR_DEVICE_TIMEOUT = 10000;

    public Result getSensorState() {
        if (SensorActivityFilter.lastActivity == 0) {
            return notFound();
        } else if (System.currentTimeMillis() - SensorActivityFilter.lastActivity < SENSOR_DEVICE_TIMEOUT) {
            return ok();
        } else {
            return noContent();
        }
    }

}
