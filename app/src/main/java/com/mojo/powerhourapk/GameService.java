package com.mojo.powerhourapk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GameService extends Service {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private TimedFunctions timers = new TimedFunctions(getApplicationContext());
    private Media media = new Media();
    //public static Notification notification = new Notification(this);

    public GameService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
