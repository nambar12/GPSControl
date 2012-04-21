package org.nambar.gpscontrol.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.nambar.gpscontrol.GPSControlExceptionHandler;
import org.nambar.gpscontrol.R;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class GPSControlService extends Service
{
	private final IBinder binder = new GPSControlServiceBinder(this);
	private AbstractLocationWorker worker = null;
	private LocationManager locationManager;
	private WakeLock cpulock = null;
	private GPSControlServiceState state;
	private boolean initialized = false;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if(!initialized) init();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		try
		{
			locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
		}
		catch(Exception e) {}
	}
	
	private void init()
	{
		state = GPSControlServiceState.IDLE;
		GPSControlNotificationManager.getInstance().setContext(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);   
		cpulock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GPSControl keep alive");
		initialized = true;
		
	}

	public void play(String file)
	{
		try
		{
			stop();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			worker = new LocationPlayer(this, locationManager, reader, cpulock);
			worker.start();
			state = GPSControlServiceState.PLAYING;
		}
		catch(Exception ex)
		{
			GPSControlExceptionHandler.showException(ex);
		}
	}


	public void record(String file)
	{
		try
		{
			stop();
			PrintWriter writer = new PrintWriter(new FileWriter(file, false));
			worker = new LocationRecorder(this, locationManager, writer, cpulock);
			worker.start();
			state = GPSControlServiceState.RECORDING;
			Notification notification = GPSControlNotificationManager.getInstance().getNotification(R.string.record, R.drawable.record);
			startForeground(R.string.NOTIFICATION, notification);
		}
		catch(Throwable ex)
		{
			GPSControlExceptionHandler.showException(ex);
		}
	}

	public GPSControlServiceStatus getStatus()
	{
		switch(state)
		{
			case IDLE :
				return new GPSControlServiceStatus(null, 0, state);
			case RECORDING :
			case PLAYING :
				return new GPSControlServiceStatus(worker.getLocation(), worker.getTimePassed(), state);
		}
		return null;
		
	}

	public void stop()
	{
		if(worker != null)
		{
			worker.stop();
			worker = null;
		}
		state = GPSControlServiceState.IDLE;
	}
	
}
