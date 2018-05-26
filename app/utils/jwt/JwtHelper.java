package utils.jwt;

import play.mvc.Http;

public interface JwtHelper {
    String getUsernameFromRequest(final Http.Request request);
    boolean validateJwt(final String jwt);
    String getJWT(final String username);
}