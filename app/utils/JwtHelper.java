package utils;

import com.typesafe.config.Config;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.inject.Inject;
import java.util.Date;

public class JwtHelper {

    private final String signingKey;

    @Inject
    public JwtHelper(final Config config) {
        this.signingKey = config.getString("security.jwtSigningKey");
    }

    public String getJWT(final String username) {
        final Date now = new Date();
        long t = now.getTime();
        final Date expirationTime = new Date(t + 1300819380); // TODO: Change the expiration time to something human-readable

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();
    }

    public boolean validateJWT(final String jwt, final String username) {
        try {
            Jwts.parser().setSigningKey(signingKey)
                    .requireSubject(username)
                    .parseClaimsJws(jwt);
            return true;
        } catch (final Exception ignore) {
            // TODO: handle exception
            return false;
        }
    }
}
