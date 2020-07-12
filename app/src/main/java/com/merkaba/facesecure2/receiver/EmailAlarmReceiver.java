package com.merkaba.facesecure2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.merkaba.facesecure2.R;
import com.merkaba.facesecure2.activity.MainActivity;
import com.merkaba.facesecure2.utils.DatabaseHelper;
import com.merkaba.facesecure2.utils.DateUtils;
import com.merkaba.facesecure2.utils.SendEmailService;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.IOException;

public class EmailAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            final boolean sendMail = intent.getBooleanExtra(MainActivity.PREF_SEND_MAIL, false);
            if(!sendMail) return;
            final String host = intent.getStringExtra(MainActivity.PREF_SMTP_HOST);
            if(!isConnectedToThisServer(host)) {
                sendNotificationFail(context, "Email server pengirim tidak merespon");
                return;
            }
            final String port = intent.getStringExtra(MainActivity.PREF_SMTP_PORT);
            final String sender = intent.getStringExtra(MainActivity.PREF_SMTP_SENDER_USER);
            final String receiver = intent.getStringExtra(MainActivity.PREF_SMTP_RECEIVER_USER);
            final String password = intent.getStringExtra(MainActivity.PREF_SMTP_SENDER_PASSWORD);
            DateUtils dt = new DateUtils("/");
            String subject1 = "Alpharai Absensi " + dt.getCurrentDate();
            final String subject = subject1.replace("/", "-");
            final String content = (new DatabaseHelper(context)).populateAttendance(dt.getCurrentDate(), dt.getCurrentDate());
            SendEmailService.getInstance(host, port, sender, receiver, password).emailExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    SendEmailService.getInstance(host, port, sender, receiver, password).SendEmail(subject, content);
//                    new Handler(Looper.getMainLooper()).post(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            DynamicToast.makeSuccess(context, "Email terkirim", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    DynamicToast.makeSuccess(context, "Email terkirim", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            sendNotificationFail(context, ex.toString());
        }
    }

    private void sendNotificationFail(Context context, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID_EMAIL)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Gagal mengirimkan email")
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }

    public boolean isConnectedToThisServer(String host) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
