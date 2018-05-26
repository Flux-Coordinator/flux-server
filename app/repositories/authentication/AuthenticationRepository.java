package repositories.authentication;

import models.User;

public interface AuthenticationRepository {

    /**
     * Checks, if the user exists in the repository.
     * @param user The user containing a username and password (unhashed).
     * @return Returns true, if the user exists in the repository. False, if the user does not exist.
     */
    boolean isUserValid(final User user);
}
