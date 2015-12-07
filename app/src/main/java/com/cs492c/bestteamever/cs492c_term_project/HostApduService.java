package com.cs492c.bestteamever.cs492c_term_project;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import static android.content.Intent.getIntent;

public class HostApduService extends android.nfc.cardemulation.HostApduService {

    private String password;

    public HostApduService() {
    }


    public int onStartCommand (Intent intent, int flags, int startId) {
        if(intent != null) {
            password = intent.getExtras().getString("password");
            if (password == null) {
                stopSelf();
            }
        }
        Toast startToast = Toast.makeText(getApplicationContext(), "Starting Service", Toast.LENGTH_SHORT);
        startToast.show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        if(selectAidApdu(commandApdu)){
            return password.getBytes();
        } else {
            return null;
        }
    }

    private boolean selectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4; // APDU 0x00a4...
    }

    @Override
    public void onDeactivated(int reason) {
    }
}
