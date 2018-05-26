package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.User;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.authentication.AuthenticationRepository;
import utils.jwt.JwtHelperImpl;

@Singleton
public class LoginController extends Controller {
    private final JwtHelperImpl jwtHelper;
    private final AuthenticationRepository authenticationRepository;

    @Inject
    public LoginController(final HttpExecutionContext httpExecutionContext, final JwtHelperImpl jwtHelper, final AuthenticationRepository authenticationRepository) {
        this.jwtHelper = jwtHelper;
        this.authenticationRepository = authenticationRepository;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result login() {
        final JsonNode jsonNode = request().body().asJson();
        final User requestedUser = Json.fromJson(jsonNode, User.class);

        if(authenticationRepository.isUserValid(requestedUser)) {
            return ok(jwtHelper.getJWT(requestedUser.getUsername()));
        }

        return unauthorized();
    }
}
