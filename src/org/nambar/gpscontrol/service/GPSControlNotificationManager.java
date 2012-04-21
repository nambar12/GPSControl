package org.nambar.gpscontrol.service;

import org.nambar.gpscontrol.GPSControlActivity;
import org.nambar.gpscontrol.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class GPSControlNotificationManager
{
	public static GPSControlNotificationManager s_instance = new GPSControlNotificationManager();
	private Context context;
	
	private GPSControlNotificationManager()
	{
	}

	public static GPSControlNotificationManager getInstance()
	{
		return s_instance;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}
	
	 public Notification getNotification(int messageID, int icon)
	 {
		 CharSequence message = context.getText(messageID);
		 Notification notification = new Notification(icon, message, System.currentTimeMillis());
		 notification.flags |= Notification.FLAG_ONGOING_EVENT;
		 PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, GPSControlActivity.class), 0);
		 notification.setLatestEventInfo(context, context.getText(R.string.app_name), message, contentIntent);
		 return notification;
	 }

//	 public void showNotification(int messageID, int icon)
//	 {
//		 showNotification(context.getText(messageID).toString(), icon);
//	 }
//
//	 public void showNotification(String message, int icon)
//	 {
//		 NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//		 Notification notification = new Notification(icon, message, System.currentTimeMillis());
//		 notification.flags |= Notification.FLAG_ONGOING_EVENT;
//		 PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, GPSControlActivity.class), 0);
//		 notification.setLatestEventInfo(context, context.getText(R.string.app_name), message, contentIntent);
//		 notificationManager.notify(R.string.NOTIFICATION, notification);
//	 }
//
//	 public void hideNotification()
//	 {
//		 NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//		 notificationManager.cancel(R.string.NOTIFICATION);
//	 }
	 
}
