package models.authentication;

import models.User;
import org.mindrot.jbcrypt.BCrypt;

public class BcryptHashedUser extends HashedUser {


    public BcryptHashedUser(final User user) {
        super(user);
    }

    @Override
    protected String hashString(final String string) {
        return BCrypt.hashpw(string, BCrypt.gensalt());
    }

    @Override
    public boolean validateUser(String username, String unhashedPassword) {
        return getUsername().equals(username) && BCrypt.checkpw(unhashedPassword, this.getPassword());
    }
}
