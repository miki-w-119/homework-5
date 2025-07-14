package data_access;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import entity.User;
import entity.UserFactory;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

/**
 * DAO for user data implemented using a CSV file.
 */
public class FileUserDataAccessObject
        implements SignupUserDataAccessInterface,
        LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface {

    private static final String HEADER = "username,password";

    private final File csvFile;
    private final Map<String, Integer> headers = new LinkedHashMap<>();
    private final Map<String, User> accounts = new HashMap<>();

    /**
     * Constructs the DAO, creating the file if empty, or loading existing users.
     *
     * @param csvPath     Path to the CSV file.
     * @param userFactory Factory to create User instances.
     * @throws IOException if file I/O fails.
     * @throws RuntimeException 1346
     */
    public FileUserDataAccessObject(String csvPath, UserFactory userFactory) throws IOException {
        this.csvFile = new File(csvPath);
        headers.put("username", 0);
        headers.put("password", 1);

        if (csvFile.length() == 0) {
            saveToFile();
        }
        else {
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                final String headerLine = reader.readLine();
                if (!HEADER.equals(headerLine)) {
                    throw new RuntimeException(
                            String.format("Header mismatch: expected \"%s\" but was \"%s\"", HEADER, headerLine));
                }
                String row;
                while ((row = reader.readLine()) != null) {
                    final String[] cols = row.split(",");
                    final String name = cols[headers.get("username")];
                    final String pwd = cols[headers.get("password")];
                    accounts.put(name, userFactory.create(name, pwd));
                }
            }
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // write header
            writer.write(HEADER);
            writer.newLine();
            // write each user
            for (User user : accounts.values()) {
                writer.write(user.getName() + "," + user.getPassword());
                writer.newLine();
            }
        }
        catch (IOException exception) {
            throw new RuntimeException("Failed to save user accounts", e);
        }
    }

    @Override
    public boolean existsByName(String username) {
        return accounts.containsKey(username);
    }

    @Override
    public User get(String username) {
        return accounts.get(username);
    }

    @Override
    public void save(User user) {
        accounts.put(user.getName(), user);
        saveToFile();
    }

    @Override
    public void changePassword(User user) {
        accounts.put(user.getName(), user);
        saveToFile();
    }

    // ─── Task 2.1 stubs ──────────────────────────────────────────────────────

    /**
     * No‐op for file‐based DAO; login tracking not persisted here.
     */
    @Override
    public void setCurrentUser(String username) {
        // intentionally left blank
    }

    /**
     * Always returns null in this implementation.
     */
    @Override
    public String getCurrentUser() {
        return null;
    }
}
