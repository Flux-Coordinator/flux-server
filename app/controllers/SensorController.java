package controllers;

import authentication.JWTAuthenticator;
import filters.SensorActivityFilter;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import java.util.Date;

@Security.Authenticated(value = JWTAuthenticator.class)
public class SensorController extends Controller {

    private static final long SENSOR_DEVICE_TIMEOUT = 10000;

    public Result getSensorState() {
        Date currentDate = new Date();
        if (SensorActivityFilter.lastActivity != null && currentDate.getTime() - SensorActivityFilter.lastActivity.getTime() < SENSOR_DEVICE_TIMEOUT) {
            return ok("Sensor is active");
        } else {
            return notFound("No sensor connected");
        }
    }

}
