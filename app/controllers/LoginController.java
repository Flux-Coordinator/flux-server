package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.LoginRequest;
import models.Project;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.JwtHelper;

@Singleton
public class LoginController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final JwtHelper jwtHelper;

    private static final LoginRequest fakeUser;

    static {
        fakeUser = new LoginRequest("user", "secret");
    }

    @Inject
    public LoginController(final HttpExecutionContext httpExecutionContext, final JwtHelper jwtHelper) {
        this.httpExecutionContext = httpExecutionContext;
        this.jwtHelper = jwtHelper;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result login() {
        final JsonNode jsonNode = request().body().asJson();
        final LoginRequest request = Json.fromJson(jsonNode, LoginRequest.class);

        // TODO: Check Username and Password are correct
        if(request.equals(fakeUser)) {
            return ok(jwtHelper.getJWT(request.getUsername()));
        }

        return unauthorized();
    }

    public Result validateToken() {
        String jwt = request().header("Authorization").orElse("");
        jwt = jwt.replaceFirst("Bearer ", "");
        if (jwtHelper.validateJWT(jwt, fakeUser.getUsername())) {
            return ok("");
        } else {
            return unauthorized();
        }
    }
}
