package repositories.authentication;

import models.User;
import models.authentication.BcryptHashedUser;
import models.authentication.HashedUser;

public class LocalAuthenticationRepository implements AuthenticationRepository {
    private static final HashedUser localUser;

    static {
        localUser = new BcryptHashedUser(new User("user", "secret"));
    }

    @Override
    public boolean isUserValid(final User user) {
        return localUser.validateUser(user.getUsername(), user.getPassword());
    }
}