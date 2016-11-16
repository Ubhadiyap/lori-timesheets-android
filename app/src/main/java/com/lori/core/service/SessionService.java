package com.lori.core.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lori.core.app.App;
import com.lori.core.entity.User;
import com.lori.core.gate.lori.exception.LoriAuthenticationException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.UUID;

/**
 * @author artemik
 */
@Singleton
public class SessionService {

    private static final String TAG = SessionService.class.getSimpleName();

    private static final String SESSION_KEY = "SESSION_KEY";
    private static final String USER_KEY = "USER_KEY";
    private static final String SERVER_URL_KEY = "SERVER_URL_KEY";

    @Inject
    App app;

    @Inject
    public SessionService() {
    }

    public void setServerUrl(String serverUrl) {
        putToPreferences(SERVER_URL_KEY, serverUrl);
    }

    public String getServerUrl() {
        return readFromPreferences(SERVER_URL_KEY);
    }

    public void setSession(UUID session) {
        putToPreferences(SESSION_KEY, session == null ? null : session.toString());
    }

    public UUID clearSession() {
        UUID deletedSession = getSession();
        setSession(null);
        setUser(null);
        setServerUrl(null);
        return deletedSession;
    }

    public boolean hasSession() {
        return getSession() != null &&
                getUser() != null &&
                getServerUrl() != null;
    }

    public UUID getSession() {
        String session = readFromPreferences(SESSION_KEY);
        return session == null ? null : UUID.fromString(session);
    }

    public UUID getSessionEx() throws LoriAuthenticationException {
        UUID session = getSession();
        if (session == null) {
            throw new LoriAuthenticationException("No session");
        }
        return session;
    }

    public User getUser() {
        String userJson = readFromPreferences(USER_KEY);
        if (userJson == null) {
            return null;
        }

        try {
            //TODO: get rid of json conversion per each getUser() call.
            return new ObjectMapper().readValue(userJson, User.class);
        } catch (IOException e) {
            String msg = "User read error";
            Log.e(TAG, msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public void setUser(User user) {
        String userJson;
        try {
            userJson = new ObjectMapper().writeValueAsString(user);
        } catch (JsonProcessingException e) {
            String msg = "User save error";
            Log.e(TAG, msg, e);
            throw new RuntimeException(msg, e);
        }

        putToPreferences(USER_KEY, userJson);
    }

    private void putToPreferences(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String readFromPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferences();
        return sharedPreferences.getString(key, null);
    }

    private SharedPreferences getPreferences() {
        return app.getSharedPreferences(SessionService.class.getName(), Context.MODE_PRIVATE);
    }
}
