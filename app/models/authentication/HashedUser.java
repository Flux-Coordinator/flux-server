package models.authentication;

import models.User;

import java.util.Objects;

public abstract class HashedUser {
    private String username;
    private String password;

    public HashedUser(final User user) {
        this.setUsername(user.getUsername());
        this.setPassword(hashString(user.getPassword()));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    protected abstract String hashString(final String string);

    public abstract boolean validateUser(final String username, final String unhashedPassword);

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashedUser that = (HashedUser) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
