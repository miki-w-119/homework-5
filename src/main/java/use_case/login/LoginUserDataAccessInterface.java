package use_case.login;

import entity.User;
import use_case.signup.SignupUserDataAccessInterface;

/**
 * The interface for Login data access:
 *  - user lookup
 *  - current‐user tracking (for “who’s logged in”)
 * We also extend the Signup DAO interface so that the Test’s call to
 *   userRepository.save(user);
 * will compile.
 */
public interface LoginUserDataAccessInterface
        extends SignupUserDataAccessInterface {

    /**
     * Returns true if a user with this name exists.
     *
     * @param username the name to look up
     * @return whether that user is present
     */
    boolean existsByName(String username);

    /**
     * Fetches the stored User entity for this username.
     *
     * @param username the name of the user
     * @return the User object (never null if existsByName was true)
     */
    User get(String username);

    /**
     * Records which user is currently logged in.
     *
     * @param username the user name to mark as current
     */
    void setCurrentUser(String username);

    /**
     * Retrieves who is currently logged in.
     *
     * @return the username, or null if no one is logged in
     */
    String getCurrentUser();
}
