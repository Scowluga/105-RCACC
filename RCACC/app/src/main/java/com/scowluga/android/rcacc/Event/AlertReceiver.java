package com.scowluga.android.rcacc.Event;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.scowluga.android.rcacc.Main.MainActivity;
import com.scowluga.android.rcacc.R;

/**
 * Created by robertlu on 2016-11-20.
 */
public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction(); // get the action

        String result = "Get information about your event here!"; // if the event name is not received.
        if (action.equals("event_broadcast_string")) {
            result = intent.getExtras().getString("name"); //set info as the name
        }
        createNotification(context, "Cadet Event Coming Up!", result, "Cadet Event!",
                intent.getExtras().getInt("color"));
    }

    public void createNotification(Context context, String s, String s1, String alert, int color) {

        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.calendar) //logo. Changeable
                .setContentTitle(s) //title is just Cadet event!
                .setContentText(s1) //text is event name
                .setTicker(alert); //don't know what this is...

        mBuilder.setContentIntent(notificIntent);

        mBuilder.setColor(color);
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND); //using sound

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }
}
