package com.cs492c.bestteamever.cs492c_term_project;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;
import com.samsung.android.sdk.pass.SpassInvalidStateException;

import haibison.android.lockpattern.LockPatternActivity;
import haibison.android.lockpattern.utils.AlpSettings;

public class MainActivity extends Activity {


    private SpassFingerprint mSpassFingerprint;
    private Spass mSpass;
    private boolean onReadyIdentify = false;
    private boolean onReadyEnroll = false;
    boolean isFeatureEnabled = false;

    private static final int REQ_CREATE_PATTERN = 1;
    private static final int REQ_ENTER_PATTERN = 2;
    public static final String PREFS_NAME = "MyPrefs";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Save the pattern in shared preferences
        AlpSettings.Security.setAutoSavePattern(this, true);

        //Initialization of the fingerprint reader.
        mSpass = new Spass();
        try {
            mSpass.initialize(MainActivity.this);
        } catch (SsdkUnsupportedException e) {
            Log.i("FINGERPRINT", "Exception: " + e);
        } catch (UnsupportedOperationException e){
            Log.i("FINGERPRINT", "Fingerprint Service is not supported in the device");
        }
        isFeatureEnabled = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if(isFeatureEnabled){
            mSpassFingerprint = new SpassFingerprint(MainActivity.this);
        }


        final Button fingerPrintButton = (Button) findViewById(R.id.fingerprintButton);
        fingerPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!mSpassFingerprint.hasRegisteredFinger()) {
                        Log.i("FINGERPRINT", "Please register finger first");
                        Toast noFingerprintToast = Toast.makeText(getApplicationContext()
                                , "Please register a fingerprint first", Toast.LENGTH_SHORT);
                        noFingerprintToast.show();
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    } else {
                        if (onReadyIdentify == false) {
                            onReadyIdentify = true;
                            try {
                                mSpassFingerprint.startIdentifyWithDialog(MainActivity.this, listener, true);
                                Log.i("FINGERPRINT", "Please identify finger to verify you");
                            } catch (IllegalStateException e) {
                                onReadyIdentify = false;
                                Log.i("FINGERPRINT", "Exception: " + e);
                            }
                        } else {
                            Log.i("FINGERPRINT", "Please cancel Identify first");
                        }
                    }
                } catch (UnsupportedOperationException e) {
                    Log.i("FINGERPRINT", "Fingerprint Service is not supported in the device");
                    Toast failToast = Toast.makeText(getApplicationContext()
                            , "Fingerprinting is not supported on this device", Toast.LENGTH_SHORT);
                    failToast.show();
                }
            }
        });

//        createNewPattern();

        final Button unlockPatternButton = (Button) findViewById(R.id.patternButton);
        unlockPatternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comparePattern();
            }
        });

        final Button newPatternButton = (Button) findViewById(R.id.createPatternButton);
        newPatternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPattern();
            }
        });
        checkFirstRun();
    }


    private SpassFingerprint.IdentifyListener listener = new SpassFingerprint.IdentifyListener() {
        @Override
        /**
         * Handling the response of reading the fingerprint.
         */
        public void onFinished(int eventStatus) {
            onReadyIdentify = false;
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS || eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                Log.i("FINGERPRINT","onFinished() : Identify authentification Success");
                startNFCService();
                Toast succesToast = Toast.makeText(getApplicationContext()
                        , "Correct fingerprint found", Toast.LENGTH_SHORT);
                succesToast.show();
            } else {
                Log.i("FINGERPRINT", "onFinished() : Authentification Fail for identify");
            }
        }

        @Override
        public void onReady() {
        }

        @Override
        public void onStarted() {
            Log.i("FINGERPRINT", "User touched fingerprint sensor!");
        }
    };
    /*
    Can probably be deleted?
     */
    private SpassFingerprint.RegisterListener mRegisterListener = new SpassFingerprint.RegisterListener() {

        @Override
        public void onFinished() {
            onReadyEnroll = false;
        }
    };


    /**
     * Start the service and supply the password.
     */
    private void startNFCService() {
        Intent nfc = new Intent(this, HostApduService.class);
        nfc.putExtra("password", getPassword());
        startService(nfc);
    }

    private void createNewPattern() {
        LockPatternActivity.IntentBuilder.newPatternCreator(this)
                .startForResult(this, REQ_CREATE_PATTERN);
    }

    private void comparePattern() {
        LockPatternActivity.IntentBuilder.newPatternComparator(this).startForResult(this, REQ_ENTER_PATTERN);
    }

    @Override
    /**
     * Handling the response from the unlock pattern.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_ENTER_PATTERN: {
                /**
                 * NOTE that there are 4 possible result codes!!!
                 */
                switch (resultCode) {
                    case RESULT_OK:
                        // The user passed
                        Log.i("PATTERN", "Enteredpattern successfully");
                        startNFCService();
                        Toast succesToast = Toast.makeText(getApplicationContext()
                                , "Pattern entered successfully", Toast.LENGTH_SHORT);
                        succesToast.show();
                        break;
                    case RESULT_CANCELED:
                        // The user cancelled the task
                        Log.i("PATTERN", "Pattern was cancelled");
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        // The user failed to enter the pattern
                        Log.i("PATTERN", "User failed to enter correct pattern");
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        // The user forgot the pattern and invoked your recovery Activity.
                        break;
                }

                /**
                 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
                 * the number of tries that the user did.
                 */
                int retryCount = data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

                break;
            }// REQ_ENTER_PATTERN
        }
    }

    /**
     * Check if the application is started for the first time, OR if no password is set.
     */
    private void checkFirstRun() {
        boolean firstRun = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean("firstRun", true);
        if(firstRun){
            //To some first time instructions on usage
            startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
            if(getPassword() != null) {
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putBoolean("firstRun", false).apply();
            }
        }
    }

    /**
     * Gets the password from shared preferences.
     * @return the current password stored in shared preferences.
     */
    private String getPassword() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString("password", null);
    }

    /**
     * Sets the password in shared preferences.
     * @param password the password you want to set.
     */
    public void setPassword(String password) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putString("password", password).apply();
    }

    @Override
    /**
     * Stop the service after the app is closed.
     */
    public void onDestroy() {
        stopService(new Intent(MainActivity.this, HostApduService.class));
        super.onDestroy();
    }

    @Override
    /**
     * Stop the service when the app is not in the foreground anymore.
     */
    public void onPause() {
        stopService(new Intent(MainActivity.this, HostApduService.class));
        super.onPause();
    }




}
