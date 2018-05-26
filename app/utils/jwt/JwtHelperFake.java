package utils.jwt;

import play.mvc.Http;

public class JwtHelperFake implements JwtHelper {
    @Override
    public String getUsernameFromRequest(final Http.Request request) {
        return "user";
    }

    @Override
    public boolean validateJwt(final String jwt) {
        return true;
    }

    @Override
    public String getJWT(final String username) {
        return "";
    }
}
