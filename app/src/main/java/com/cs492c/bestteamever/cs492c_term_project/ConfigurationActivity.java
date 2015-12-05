package com.cs492c.bestteamever.cs492c_term_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                String password = passwordText.toString();
                getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit().putString("password", password).apply();
                startActivity(new Intent(ConfigurationActivity.this, MainActivity.class));
            }
        });


    }
}
