package org.nambar.gpscontrol.service;

import java.io.PrintWriter;

import org.nambar.gpscontrol.GPSControlExceptionHandler;
import org.nambar.gpscontrol.GPSControlParams;
import org.nambar.gpscontrol.R;

import android.app.Notification;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;

public class LocationRecorder extends AbstractLocationWorker implements LocationListener
{
	private Location lastLocation;
	private Object locationLock = new Object();
	PrintWriter writer = null;
	private GPSControlService service;
	
	public LocationRecorder(GPSControlService service, LocationManager locationManager, PrintWriter writer, WakeLock cpulock)
	{
		super("RecorderThread", locationManager, cpulock);
		this.writer = writer;
		this.service = service;
	}
	
	protected void startSpecific()
	{
		Notification notification = GPSControlNotificationManager.getInstance().getNotification(R.string.record, R.drawable.record);
		service.startForeground(R.string.NOTIFICATION, notification);
		try
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}
		catch (Exception e) { GPSControlExceptionHandler.showException(e); }
	}

	protected void stopSpecific()
	{
		service.stopForeground(true);
		locationManager.removeUpdates(this);
		if(writer != null) writer.close();
	}

	protected boolean doWork()
	{
		synchronized(locationLock)
		{
			String line;
			if(lastLocation != null)
			{
				line = createLine();
			}
			else
			{
				line = GPSControlParams.NO_LOCATION_STR;
			}
			writer.println(line);
			location = lastLocation;
			return true;
		}
	}

	private String createLine()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(lastLocation.getLatitude());
		sb.append(GPSControlParams.FIELD_DELIM);
		sb.append(lastLocation.getLongitude());
		sb.append(GPSControlParams.FIELD_DELIM);
		if(lastLocation.hasAltitude()) { sb.append(lastLocation.getAltitude()); } else { sb.append(GPSControlParams.NO_VALUE); }
		sb.append(GPSControlParams.FIELD_DELIM);
		if(lastLocation.hasSpeed()) { sb.append(lastLocation.getSpeed()); } else { sb.append(GPSControlParams.NO_VALUE); }
		sb.append(GPSControlParams.FIELD_DELIM);
		if(lastLocation.hasAccuracy()) { sb.append(lastLocation.getAccuracy()); } else { sb.append(GPSControlParams.NO_VALUE); }
		sb.append(GPSControlParams.FIELD_DELIM);
		if(lastLocation.hasBearing()) { sb.append(lastLocation.getBearing()); } else { sb.append(GPSControlParams.NO_VALUE); }
		return sb.toString();
	}

	@Override
	public void onLocationChanged(Location location)
	{
		synchronized(locationLock)
		{
			lastLocation = location;
		}
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

}
