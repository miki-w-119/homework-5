package data_access;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import entity.User;
import entity.UserFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

/**
 * The DAO for user data, backed by an external REST API.
 */
public class DBUserDataAccessObject implements SignupUserDataAccessInterface,
        LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface {
    private static final int SUCCESS_CODE = 200;
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String STATUS_CODE_LABEL = "status_code";
    private static final String USERNAME_LABEL = "username";
    private static final String PASSWORD_LABEL = "password";
    private static final String MESSAGE_LABEL = "message";

    private final UserFactory userFactory;

    public DBUserDataAccessObject(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    @Override
    public User get(String username) {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(String.format("http://vm003.teach.cs.toronto.edu:20112/user?username=%s", username))
                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_JSON)
                .build();
        try {
            final Response response = client.newCall(request).execute();
            final JSONObject body = new JSONObject(response.body().string());
            if (body.getInt(STATUS_CODE_LABEL) == SUCCESS_CODE) {
                final JSONObject userJson = body.getJSONObject("user");
                final String name = userJson.getString(USERNAME_LABEL);
                final String pwd = userJson.getString(PASSWORD_LABEL);
                return userFactory.create(name, pwd);
            }
            else {
                throw new RuntimeException(body.getString(MESSAGE_LABEL));
            }
        }
        catch (IOException | JSONException ex) {
            throw new RuntimeException("Failed to fetch user", ex);
        }
    }

    @Override
    public boolean existsByName(String username) {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(String.format("http://vm003.teach.cs.toronto.edu:20112/checkIfUserExists?username=%s", username))
                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_JSON)
                .build();
        try {
            final Response response = client.newCall(request).execute();
            final JSONObject body = new JSONObject(response.body().string());
            return body.getInt(STATUS_CODE_LABEL) == SUCCESS_CODE;
        }
        catch (IOException | JSONException ex) {
            throw new RuntimeException("Failed to check user existence", ex);
        }
    }

    @Override
    public void save(User user) {
        final OkHttpClient client = new OkHttpClient();
        final MediaType mediaType = MediaType.parse(CONTENT_TYPE_JSON);
        final JSONObject obj = new JSONObject();
        obj.put(USERNAME_LABEL, user.getName());
        obj.put(PASSWORD_LABEL, user.getPassword());
        final RequestBody rb = RequestBody.create(obj.toString(), mediaType);
        final Request request = new Request.Builder()
                .url("http://vm003.teach.cs.toronto.edu:20112/user")
                .method("POST", rb)
                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_JSON)
                .build();
        try {
            final Response response = client.newCall(request).execute();
            final JSONObject body = new JSONObject(response.body().string());
            if (body.getInt(STATUS_CODE_LABEL) != SUCCESS_CODE) {
                throw new RuntimeException(body.getString(MESSAGE_LABEL));
            }
        }
        catch (IOException | JSONException ex) {
            throw new RuntimeException("Failed to save user", ex);
        }
    }

    @Override
    public void changePassword(User user) {
        final OkHttpClient client = new OkHttpClient();
        final MediaType mediaType = MediaType.parse(CONTENT_TYPE_JSON);
        final JSONObject obj = new JSONObject();
        obj.put(USERNAME_LABEL, user.getName());
        obj.put(PASSWORD_LABEL, user.getPassword());
        final RequestBody rb = RequestBody.create(obj.toString(), mediaType);
        final Request request = new Request.Builder()
                .url("http://vm003.teach.cs.toronto.edu:20112/user")
                .method("PUT", rb)
                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_JSON)
                .build();
        try {
            final Response response = client.newCall(request).execute();
            final JSONObject body = new JSONObject(response.body().string());
            if (body.getInt(STATUS_CODE_LABEL) != SUCCESS_CODE) {
                throw new RuntimeException(body.getString(MESSAGE_LABEL));
            }
        }
        catch (IOException | JSONException ex) {
            throw new RuntimeException("Failed to change password", ex);
        }
    }

    // ─── Task 2.1 stubs ──────────────────────────────────────────────────────

    /**
     * No‐op for tracking current user in DB version.
     */
    @Override
    public void setCurrentUser(String username) {
        // intentionally left blank
    }

    /**
     * Always returns null in the DB‐backed DAO.
     */
    @Override
    public String getCurrentUser() {
        return null;
    }
}
