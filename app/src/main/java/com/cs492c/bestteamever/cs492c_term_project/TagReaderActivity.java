package com.cs492c.bestteamever.cs492c_term_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TagReaderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_reader_activity);
    }

    public void onDestroy() {
        stopService(new Intent(TagReaderActivity.this, HostApduService.class));
        super.onDestroy();
    }

    @Override
    /**
     * Stop the service when the app is not in the foreground anymore.
     */
    public void onPause() {
        stopService(new Intent(TagReaderActivity.this, HostApduService.class));
        super.onPause();
    }

    @Override
    public void onRestart() {
        startActivity(new Intent(TagReaderActivity.this, MainActivity.class));
        super.onRestart();
    }
}
