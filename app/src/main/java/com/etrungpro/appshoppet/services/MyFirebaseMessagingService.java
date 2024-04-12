package com.etrungpro.appshoppet.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.etrungpro.appshoppet.activities.MainActivity;
import com.etrungpro.appshoppet.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // Phương thức được gọi khi nhận được tin nhắn từ máy chủ
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // Kiểm tra xem tin nhắn có thông báo không
        RemoteMessage.Notification notification = message.getNotification();
        if(notification == null) {
            return ;
        }

        // Lấy tiêu đề và nội dung của thông báo
        String strTitle = notification.getTitle();
        String strMessage = notification.getBody();

        // Gửi thông báo tới hệ thống
        sendNotification(strTitle, strMessage);
    }

    // Phương thức để gửi thông báo
    private void sendNotification(String strTitle, String strMessage) {
        // Tạo intent để mở MainActivity khi thông báo được nhấp
        Intent intent = new Intent(this, MainActivity.class) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Xây dựng thông báo
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(strTitle)
                .setContentText(strMessage)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        // Hiển thị thông báo
        Notification notification = notiBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {
            notificationManager.notify(1, notification);
        }

    }
}
