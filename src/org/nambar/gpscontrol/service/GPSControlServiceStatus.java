package org.nambar.gpscontrol.service;

import android.location.Location;

public class GPSControlServiceStatus
{
	private Location location;
	private long timePassed;
	private GPSControlServiceState state;
	
	public GPSControlServiceStatus(Location location, long timePassed, GPSControlServiceState state)
	{
		this.location = location;
		this.timePassed = timePassed;
		this.state = state;
	}

	public Location getLocation()
	{
		return location;
	}

	public long getTimePassed()
	{
		return timePassed;
	}

	public GPSControlServiceState getState()
	{
		return state;
	}
}
