package com.cs492c.bestteamever.cs492c_term_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigurationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        Button continueBut = (Button) findViewById(R.id.configContinueButton);
        continueBut.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * Save the password in shared preferences and go back to the main activity.
             */
            public void onClick(View v) {
                EditText passwordText = (EditText) findViewById(R.id.passwordText);
                String password = passwordText.getText().toString();
                Log.i("PASSWORD", "password: " + password);
                if(password.length() != 0) {
                    getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit().putString("password", password).apply();
                    startActivity(new Intent(ConfigurationActivity.this, MainActivity.class));
                } else {
                    Toast hint = Toast.makeText(ConfigurationActivity.this, "Please enter your password", Toast.LENGTH_SHORT);
                    hint.show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
    }
}
