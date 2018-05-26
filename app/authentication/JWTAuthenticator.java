package authentication;

import play.mvc.Http;
import play.mvc.Security.Authenticator;
import utils.jwt.JwtHelper;

import javax.inject.Inject;

public class JWTAuthenticator extends Authenticator {

    private final JwtHelper jwtHelper;

    @Inject
    public JWTAuthenticator(final JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    @Override
    public String getUsername(final Http.Context ctx) {
        return jwtHelper.getUsernameFromRequest(ctx.request());
    }
}
