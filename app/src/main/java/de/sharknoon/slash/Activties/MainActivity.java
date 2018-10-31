package de.sharknoon.slash.Activties;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.sharknoon.slash.Login.UserLogin;
import de.sharknoon.slash.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open Websocket connection
        UserLogin.createLoginClient(this);

        this.handleRegisterLink();
        this.handleLoginStop();
        this.handleLoginButton();

        UserLogin.disableLoadingScreen(false, this);
    }

    private void handleRegisterLink() {

        // Get text view element for registration and handover event listener
        TextView registerLink = findViewById(R.id.homeScreenRegisterLink);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Move on to registration page
                Intent goToRegisterActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(goToRegisterActivity);
            }
        });
    }

    private void handleLoginStop(){
        //Get progression bar to stop Login
        ProgressBar bar = findViewById(R.id.progressBar);
        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin.disableLoadingScreen(true, v.getContext());            }
        });

    }

    private void handleLoginButton() {

        // Get login button element and handover event listener
        Button loginButton = findViewById(R.id.homeScreenLoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Reset error message text viewer
                TextView emailErrorTextView = findViewById(R.id.homeScreenWrongEmailTextView);
                emailErrorTextView.setText("");
                TextView passwordErrorTextView = findViewById(R.id.homeScreenWrongPasswordTextView);
                passwordErrorTextView.setText("");

                // Get inserted E-Mail
                EditText emailInput = findViewById(R.id.homeScreenEmailInput);
                String insertedEmail = String.valueOf(emailInput.getText());

                // Get inserted Password
                EditText passwordInput = findViewById(R.id.homeScreenPasswordInput);
                String insertedPassword = String.valueOf(passwordInput.getText());


                boolean mailTrue = true;
                boolean passwordTrue = true;

                //False EMail
                if(insertedEmail.isEmpty()){
                    emailErrorTextView.setText(R.string.homeScreenIncorrectEmailMessage);
                    mailTrue = false;
                }

                // Empty password
                if (insertedPassword.isEmpty() || insertedPassword.length() < 8) {
                    passwordErrorTextView.setText(R.string.homeScreenIncorrectPasswordMessage);
                    passwordTrue = false;
                }

                // Try to login User
                if(mailTrue && passwordTrue) {
                    new UserLogin(insertedEmail, insertedPassword);
                    UserLogin.disableLoadingScreen(false, v.getContext());
                }
            }
        });
    }

}
