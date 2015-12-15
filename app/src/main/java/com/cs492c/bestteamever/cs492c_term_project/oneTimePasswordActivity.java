package com.cs492c.bestteamever.cs492c_term_project;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class oneTimePasswordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        final Button encrypt_btn = (Button) findViewById(R.id.encrypt_btn);
        encrypt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AES256Cipher a256 = AES256Cipher.getInstance();
                EditText passwordText = (EditText) findViewById(R.id.password_txt);
                String password = passwordText.getText().toString();
                String encryptedPWD = null;
                String decryptedPWD = null;
                try {
                    encryptedPWD = a256.AES_Encode(password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    decryptedPWD = a256.AES_Decode(encryptedPWD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("decrypted",decryptedPWD);
                Log.e("encrypted",encryptedPWD);
                TextView decryptText = (TextView) findViewById(R.id.decrypt_txt);
                decryptText.setText(encryptedPWD);
            }
        });
    }


}
