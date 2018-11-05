package de.sharknoon.slash.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import de.sharknoon.slash.Activties.HomeScreenActivity;
import de.sharknoon.slash.Activties.LoginActivity;
import de.sharknoon.slash.R;

public class LoginResponseHandler {

    private static final String SERVER_RESPONSE_STATUS_OK = "OK";
    private static final String SERVER_RESPONSE_USER_DOES_NOT_EXIST = "USER_DOES_NOT_EXIST";
    private static final String SERVER_RESPONSE_WRONG_PASSWORD = "WRONG_PASSWORD";
    private static final String SERVER_RESPONSE_USER_ALREADY_LOGGED_IN = "USER_ALREADY_LOGGED_IN";
    public static final String BUNDLE_KEY_SESSION_ID = "SessionID";

    public static void handlerResponse(String serverResponse, Context context) {
        Gson gson = new Gson();
        LoginResponse loginResponse = gson.fromJson(serverResponse, LoginResponse.class);

        TextView emailErrorTextView = ((Activity) context).findViewById(R.id.homeScreenWrongEmailTextView);
        TextView passwordErrorTextView = ((Activity) context).findViewById(R.id.homeScreenWrongPasswordTextView);

        switch (loginResponse.getStatus()) {

            case SERVER_RESPONSE_STATUS_OK:

                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Go to HomeScreen Activity and pass the session id
                        Activity loginActivity = (Activity) context;
                        Intent intent = new Intent(context, HomeScreenActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(BUNDLE_KEY_SESSION_ID, loginResponse.getSessionid());
                        intent.putExtras(bundle);
                        loginActivity.startActivity(intent);
                    }
                });

                Log.d("Status", SERVER_RESPONSE_STATUS_OK);
                break;
            case SERVER_RESPONSE_USER_DOES_NOT_EXIST:
                Log.d("Status", SERVER_RESPONSE_USER_DOES_NOT_EXIST);
                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        LoginActivity.disableLoadingScreen(true, context);
                        emailErrorTextView.setText(R.string.homeScreenUserDoesNotExist);
                    }
                });
                break;
            case SERVER_RESPONSE_WRONG_PASSWORD:
                Log.d("Status", SERVER_RESPONSE_WRONG_PASSWORD);
                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        LoginActivity.disableLoadingScreen(true, context);
                        passwordErrorTextView.setText(R.string.homeScreenWrongPassword);                    }
                });
                break;
            case SERVER_RESPONSE_USER_ALREADY_LOGGED_IN:
                Log.d("Status", SERVER_RESPONSE_USER_ALREADY_LOGGED_IN);
                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        LoginActivity.disableLoadingScreen(true, context);
                        emailErrorTextView.setText(R.string.homeScreenUserAlreadyLoggedIn);
                    }
                });
                break;
        }
    }
}
