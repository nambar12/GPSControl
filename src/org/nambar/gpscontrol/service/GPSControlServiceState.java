package org.nambar.gpscontrol.service;

public enum GPSControlServiceState
{
	IDLE("Idle"),
	RECORDING("Recording"),
	PLAYING("Playing");
	
	private String state;
	
	private GPSControlServiceState(String state)
	{
		this.state = state;
	}
	
	public String getState()
	{
		return state;
	}

}
