package use_case.login;

import data_access.InMemoryUserDataAccessObject;
import entity.CommonUserFactory;
import entity.User;
import entity.UserFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the LoginInteractor.
 */
public class LoginInteractorTest {

    // --------------------------------------------------
    // Existing success test
    // --------------------------------------------------
    @Test
    public void successTest() {
        // Arrange
        LoginInputData inputData = new LoginInputData("Paul", "password");

        // Use the concrete DAO so we have save(...) available
        InMemoryUserDataAccessObject userRepository =
                new InMemoryUserDataAccessObject();

        // Pre-load Paul into the repo
        UserFactory factory = new CommonUserFactory();
        User user = factory.create("Paul", "password");
        userRepository.save(user);

        // Presenter that asserts on success
        LoginOutputBoundary successPresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData outputData) {
                assertEquals("Paul", outputData.getUsername());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        // Act
        LoginInputBoundary interactor =
                new LoginInteractor(userRepository, successPresenter);
        interactor.execute(inputData);
    }

    // --------------------------------------------------
    // NEW TEST FOR current‐user tracking
    // --------------------------------------------------
    @Test
    public void successUserLoggedInTest() {
        // Arrange
        LoginInputData inputData = new LoginInputData("Paul", "password");
        InMemoryUserDataAccessObject userRepository =
                new InMemoryUserDataAccessObject();

        // Precondition: no one is logged in yet
        assertNull(userRepository.getCurrentUser());

        // Add Paul to the repository
        UserFactory factory = new CommonUserFactory();
        User user = factory.create("Paul", "password");
        userRepository.save(user);

        // Presenter stub
        LoginOutputBoundary presenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData outputData) {
                assertEquals("Paul", outputData.getUsername());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        // Act
        LoginInputBoundary interactor =
                new LoginInteractor(userRepository, presenter);
        interactor.execute(inputData);

        // Assert: currentUser was recorded
        assertEquals("Paul", userRepository.getCurrentUser());
    }

    // --------------------------------------------------
    // Existing failure test
    // --------------------------------------------------
    @Test
    public void failureUserDoesNotExistTest() {
        // Arrange
        LoginInputData inputData = new LoginInputData("Paul", "password");
        InMemoryUserDataAccessObject userRepository =
                new InMemoryUserDataAccessObject();

        // No user added to repo ⇒ should fail
        LoginOutputBoundary failurePresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Paul: Account does not exist.", error);
            }
        };

        // Act
        LoginInputBoundary interactor =
                new LoginInteractor(userRepository, failurePresenter);
        interactor.execute(inputData);
    }
}
