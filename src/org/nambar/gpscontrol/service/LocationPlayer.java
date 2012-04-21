package org.nambar.gpscontrol.service;

import java.io.BufferedReader;
import java.io.IOException;

import org.nambar.gpscontrol.GPSControlExceptionHandler;
import org.nambar.gpscontrol.GPSControlParams;
import org.nambar.gpscontrol.R;

import android.app.Notification;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class LocationPlayer extends AbstractLocationWorker
{
	private static String TAG =  "GPSControlService.LocationPlayer";
	BufferedReader reader = null;
	private GPSControlService service;
	
	public LocationPlayer(GPSControlService service, LocationManager locationManager, BufferedReader reader, WakeLock cpulock)
	{
		super("PlayerThread", locationManager, cpulock);
		this.reader = reader;
		this.service = service;
	}
	
	protected void startSpecific()
	{
		Log.i(TAG, "Location player started");
		try
		{
			locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, true, true, true, 0, 5);
			locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
			Notification notification = GPSControlNotificationManager.getInstance().getNotification(R.string.playback, R.drawable.play);
			service.startForeground(R.string.NOTIFICATION, notification);
		}
		catch (Exception e) { GPSControlExceptionHandler.showException(e); }
	}

	protected void stopSpecific()
	{
		Log.i(TAG, "Location player stopped");
		service.stopForeground(true);
		try
		{
			locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
			if(reader != null) reader.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	protected boolean doWork()
	{
		String line;
		try
		{
			line = reader.readLine();
		}
		catch (IOException e)
		{
			Log.e(TAG, "Cannot read file", e);
			return false;
		}
		if(line == null)
		{
			Log.i(TAG, "Playback completed");
			try { locationManager.removeTestProvider(LocationManager.GPS_PROVIDER); } catch(Exception e) {}
			return false;
		}

		if(line.equals(GPSControlParams.NO_LOCATION_STR))
		{
			locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.TEMPORARILY_UNAVAILABLE, null, System.currentTimeMillis());
			return true;
		}
		
		String[] tokens = line.split(" ");
		Location newLocation = new Location(LocationManager.GPS_PROVIDER);
		newLocation.setLatitude(Double.valueOf(tokens[0]));
		newLocation.setLongitude(Double.valueOf(tokens[1]));
		if(!tokens[2].equals(GPSControlParams.NO_VALUE)) newLocation.setAltitude(Double.valueOf(tokens[2]));
		if(!tokens[3].equals(GPSControlParams.NO_VALUE)) newLocation.setSpeed(Float.valueOf(tokens[3]));
		if(!tokens[4].equals(GPSControlParams.NO_VALUE)) newLocation.setAccuracy(Float.valueOf(tokens[4]));
		if(tokens.length > 5 && !tokens[5].equals(GPSControlParams.NO_VALUE)) newLocation.setBearing(Float.valueOf(tokens[5]));
		newLocation.setTime(System.currentTimeMillis());
		newLocation.setExtras(new Bundle());
		locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
		location = newLocation;
		locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
		return true;
	}

}
