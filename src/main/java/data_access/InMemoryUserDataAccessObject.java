package data_access;

import java.util.HashMap;
import java.util.Map;

import entity.User;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

/**
 * In-memory DAO for signup, login, and change-password.
 */
public class InMemoryUserDataAccessObject
        implements SignupUserDataAccessInterface,
        LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface {

    private final Map<String, User> users = new HashMap<>();
    private String currentUser;

    // ─── SignupUserDataAccessInterface ────────────────────────────────────
    @Override
    public boolean existsByName(String username) {
        return users.containsKey(username);
    }

    @Override
    public void save(User user) {
        users.put(user.getName(), user);
    }

    // ─── LoginUserDataAccessInterface ─────────────────────────────────────
    @Override
    public User get(String username) {
        return users.get(username);
    }

    @Override
    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    @Override
    public String getCurrentUser() {
        return currentUser;
    }

    // ─── ChangePasswordUserDataAccessInterface ───────────────────────────
    @Override
    public void changePassword(User user) {
        // overwrite entry with updated password
        users.put(user.getName(), user);
    }
}
