package use_case.login;

import entity.User;

/**
 * The interactor for the Login use case.
 */
public class LoginInteractor implements LoginInputBoundary {
    private final LoginUserDataAccessInterface userDataAccessObject;
    private final LoginOutputBoundary loginPresenter;

    /**
     * 12345.
     * @param dao       the DAO that handles user lookup and current‐user tracking
     * @param presenter the presenter to receive success/failure callbacks
     */
    public LoginInteractor(LoginUserDataAccessInterface dao,
                           LoginOutputBoundary presenter) {
        this.userDataAccessObject = dao;
        this.loginPresenter = presenter;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();

        // 1) User must exist
        if (!userDataAccessObject.existsByName(username)) {
            loginPresenter.prepareFailView(username + ": Account does not exist.");
            return;
        }

        // 2) Check password
        final User user = userDataAccessObject.get(username);
        if (!password.equals(user.getPassword())) {
            loginPresenter.prepareFailView("Incorrect password for \"" + username + "\".");
            return;
        }

        // 3) Record who just logged in
        userDataAccessObject.setCurrentUser(user.getName());

        // 4) Prepare and present successful-login output
        final LoginOutputData outputData = new LoginOutputData(user.getName(), false);
        loginPresenter.prepareSuccessView(outputData);
    }
}
