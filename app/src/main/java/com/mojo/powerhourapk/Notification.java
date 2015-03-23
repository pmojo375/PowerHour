package com.mojo.powerhourapk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Mojsiejenko on 3/21/15.
 */
public class Notification {

    public final int mId = 1;
    private final Context context;
    // notification variables
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotificationManager;

    public Notification(Context context) {
        this.context = context;
    }

    // creates the ongoing notification
    void createNotification() {
        // needed for any notification
        Intent intent = new Intent(context, MainActivity.class);
        mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.beer_glass)
                        .setContentTitle("Powerhour - Time: " + Integer.toString(MainActivity.gametimer.getMin()) + ":" + String.format("%02d", MainActivity.gametimer.getSec() - (MainActivity.gametimer.getMin() * 60)))
                        .setContentText("Press to see app...")
                        .setPriority(2);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, mId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());

        mBuilder.setOngoing(true);
    }
}
