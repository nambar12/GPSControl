package org.nambar.gpscontrol;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

public class GPSControlExceptionHandler implements UncaughtExceptionHandler 
{
	private static GPSControlExceptionHandler instance = new GPSControlExceptionHandler(); 
	private static Context context;
	private static Handler handler;
	
	private GPSControlExceptionHandler()
	{
	}
	
	public static GPSControlExceptionHandler getInstance()
	{
		return instance;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex)
	{
		showException(ex);
	}
	
	public static void showException(final Throwable ex)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		showMessage(sw.toString());
	}

	
	public static void showMessage(final String msg)
	{
		handler.post(new Runnable()
		{
			@Override
			public void run() {
				new AlertDialog.Builder(context)
				.setPositiveButton("Close", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
					}
				})
				.setMessage(msg).show();
				
			}
		});
	}

	public static void setContext(Context ctx, Handler h)
	{
		context = ctx;
		handler = h;
	}

}
