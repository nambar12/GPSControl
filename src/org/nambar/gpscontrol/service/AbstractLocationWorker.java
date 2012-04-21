package org.nambar.gpscontrol.service;

import org.nambar.gpscontrol.GPSControlExceptionHandler;
import org.nambar.gpscontrol.GPSControlParams;

import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public abstract class AbstractLocationWorker implements Runnable
{
	private static String TAG = "GPSControlService.AbstractLocationWorker";
	private boolean shouldRun;
	private Thread thread;
	protected LocationManager locationManager;
	private WakeLock cpulock;
	protected Location location = null;
	private long startTime = 0;
	private Object pauseLock = new Object();
	private boolean paused = false;

	public AbstractLocationWorker(String name)
	{
		this(name, null, null);
	}

	public AbstractLocationWorker(String name, LocationManager locationManager, WakeLock cpulock)
	{
		thread = new Thread(this, name);
		this.locationManager = locationManager;
		this.cpulock = cpulock;
	}

	public void start()
	{
		if(cpulock != null) cpulock.acquire();
		startSpecific();
		shouldRun = true;
		startTime = System.currentTimeMillis();
		thread.start();
	}

	protected abstract void startSpecific();
	protected abstract void stopSpecific();
	protected abstract boolean doWork();
	
	public void stop()
	{
		if(cpulock != null && cpulock.isHeld()) cpulock.release();
		shouldRun = false;
		stopSpecific();
		location = null;
		startTime = 0;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public long getTimePassed()
	{
		return (System.currentTimeMillis() - startTime)/1000;
	}

	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				if(!shouldRun)
				{
					return;
				}
				if(paused)
				{
					synchronized(pauseLock)
					{
						if(paused) pauseLock.wait();
					}
				}
				if(!doWork())
				{
					stop();
					return;
				}
				Thread.sleep(GPSControlParams.SAMPLE_INTERVAL_MILLI);
			}
		}
		catch (Exception e)
		{
			Log.e(TAG,"caught exception", e);
			try { GPSControlExceptionHandler.showException(e); } catch(Throwable t) {};
		}
	}
	
	public void pause()
	{
		synchronized(pauseLock)
		{
			paused = true;
		}
	}
	
	public void resume()
	{
		synchronized(pauseLock)
		{
			paused = false;
			pauseLock.notifyAll();
		}
	}

}