package repositories.authentication;

import com.typesafe.config.Config;
import models.User;
import models.authentication.BcryptHashedUser;
import models.authentication.HashedUser;
import play.Logger;

import javax.inject.Inject;

public class LocalAuthenticationRepository implements AuthenticationRepository {
    private static final String USERNAME_KEY = "flux.user.username";
    private static final String PASSWORD_KEY = "flux.user.password";
    private static HashedUser localUser;

    @Inject
    public LocalAuthenticationRepository(final Config config) {
        if(localUser == null) {
            String username;
            String password;

            if(config.hasPath(USERNAME_KEY)) {
                username = config.getString(USERNAME_KEY);
            } else {
                username = "user";
                Logger.warn("Using an insecure username! Please configure a user in the configuration file. " +
                        "Use the following key: " + USERNAME_KEY);
            }
            if(config.hasPath(PASSWORD_KEY)) {
                password = config.getString(PASSWORD_KEY);
            } else {
                password = "secret";
                Logger.warn("Using an insecure password! Please configure a password in the configuration file. " +
                        "Use the following key: " + PASSWORD_KEY);
            }
            localUser = new BcryptHashedUser(new User(username, password));
        }
    }

    @Override
    public boolean isUserValid(final User user) {
        return localUser.validateUser(user.getUsername(), user.getPassword());
    }
}