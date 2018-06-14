package utils.jwt;

import com.typesafe.config.Config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import play.mvc.Http;

import javax.inject.Inject;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Date;

public class JwtHelperImpl implements JwtHelper {
    private final static TemporalAmount TIME_UNTIL_EXPIRATION = Period.ofWeeks(2);

    private final String signingKey;

    @Inject
    public JwtHelperImpl(final Config config) {
        this.signingKey = config.getString("security.jwtSigningKey");
    }

    @Override
    public String getJWT(final String username) {
        final Date now = new Date();
        final Date expirationTime = Date.from(now.toInstant().plus(TIME_UNTIL_EXPIRATION));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();
    }


    private String getUsername(final String jwt) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(signingKey)
                    .parseClaimsJws(jwt).getBody();
            return claims.getSubject();
        } catch(final Exception ignore) {
            return null;
        }
    }

    @Override
    public String getUsernameFromRequest(final Http.Request request) {
        String jwt = request.header("Authorization").orElse("");
        jwt = jwt.replaceFirst("Bearer ", "");
        return getUsername(jwt);
    }

    @Override
    public boolean validateJwt(final String jwt) {
        try {
            Jwts.parser().setSigningKey(signingKey)
                    .parseClaimsJws(jwt);
            return true;
        } catch (final Exception ignore) {
            return false;
        }
    }
}
