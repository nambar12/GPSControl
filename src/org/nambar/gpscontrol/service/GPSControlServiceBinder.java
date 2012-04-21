package org.nambar.gpscontrol.service;

import android.os.Binder;

public class GPSControlServiceBinder extends Binder
{
	private GPSControlService service;
	
	public GPSControlServiceBinder(GPSControlService service)
	{
		this.service = service;
	}
	
	public void play(String filename)
	{
		service.play(filename);
	}

	public void record(String fileName)
	{
		service.record(fileName);
	}
	
	public GPSControlServiceStatus getStatus()
	{
		return service.getStatus();
	}

	public void stop()
	{
		service.stop();
	}
	
}
