package use_case.login;

import entity.User;

/**
 * DAO boundary for the Login use case.
 */
public interface LoginUserDataAccessInterface {

    /**
     * Check if a user with the given username exists.
     * @param username the username to look up
     * @return true if the user exists; false otherwise
     */
    boolean existsByName(String username);

    /**
     * Fetch the User entity for the given username.
     * @param username the username to retrieve
     * @return the User, or null if not found
     */
    User get(String username);

    /**
     * Record that this username is now the “current” (logged‐in) user.
     * @param username the username to set as current, or null to clear
     */
    void setCurrentUser(String username);

    /**
     * Return the username most recently passed to setCurrentUser(),
     * or null if none.
     * @return the current logged‐in username, or null
     */
    String getCurrentUser();
}
