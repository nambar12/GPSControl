package org.nambar.gpscontrol;

import java.io.File;

import org.nambar.gpscontrol.service.AbstractLocationWorker;
import org.nambar.gpscontrol.service.GPSControlService;
import org.nambar.gpscontrol.service.GPSControlServiceBinder;
import org.nambar.gpscontrol.service.GPSControlServiceState;
import org.nambar.gpscontrol.service.GPSControlServiceStatus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GPSControlActivity extends Activity
{
	private Button recordButton = null;
	private Button playButton = null;
	private TextView langTextView = null;
	private TextView longTextView = null;
	private TextView altTextView = null;
	private TextView speedTextView = null;
	private TextView accuracyTextView = null;
	private TextView bearingTextView = null;
	private TextView timeTextView = null;
	private TextView stateTextView = null;
	private TextView providerTextView = null;
	public static String selectedFile = null;
	private LocationPresenter locationPresenter = null;
	private Handler updateHandler;

	private static final int SELECT_FILE = 1000;
	private static final int SELECT_RECORD_FILE = 1001;

	private GPSControlServiceBinder binder;
	
	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			binder = (GPSControlServiceBinder)service;
			switch(binder.getStatus().getState())
			{
				case PLAYING : playButton.setText(R.string.stop); recordButton.setEnabled(false); break; 
				case RECORDING : recordButton.setText(R.string.stop); playButton.setEnabled(false); break;
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			binder = null;
		}
	};

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        addListeners();
    }
	
	@Override
	protected void onPause()
	{
		locationPresenter.pause();
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		locationPresenter.resume();
	}
	
	@Override
	protected void onDestroy()
	{
		locationPresenter.stop();
		super.onDestroy();
	}
	
	private void init()
	{
        GPSControlExceptionHandler.setContext(this, new Handler());
        final Intent serviceIntent = new Intent(this, GPSControlService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
		Thread.setDefaultUncaughtExceptionHandler(GPSControlExceptionHandler.getInstance());
		(new File(Environment.getExternalStorageDirectory(), GPSControlParams.FILES_LOCATION)).mkdirs();
		updateHandler = new Handler();
		locationPresenter = new LocationPresenter();
		locationPresenter.start();
	}

	private void addListeners()
	{
		recordButton = (Button)findViewById(R.id.ButtonRecord);
		recordButton.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
                onRecordClick();
        	}
        });
		playButton = (Button)findViewById(R.id.ButtonPlay);
		playButton.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
                onPlayClick();
        	}
        });
		
		langTextView = (TextView)findViewById(R.id.LangText);
		longTextView = (TextView)findViewById(R.id.LongText);
		altTextView = (TextView)findViewById(R.id.AltText);
		speedTextView = (TextView)findViewById(R.id.SpeedText);
		accuracyTextView = (TextView)findViewById(R.id.AccuracyText);
		bearingTextView = (TextView)findViewById(R.id.BearingText);
		timeTextView = (TextView)findViewById(R.id.TimeText);
		stateTextView = (TextView)findViewById(R.id.StateText);
		providerTextView = (TextView)findViewById(R.id.ProviderText);
	}
    
	private void onRecordClick()
	{
		try
		{
			if(binder.getStatus().getState() == GPSControlServiceState.RECORDING)
			{
				binder.stop();
				recordButton.setText("Record");
				playButton.setEnabled(true);
			}
			else
			{
				selectedFile = null;
				Intent intent = new Intent(this, RecordFileSelector.class);
				startActivityForResult(intent, SELECT_RECORD_FILE);
			}
		}
		catch(Throwable ex)
		{
			GPSControlExceptionHandler.showException(ex);
		}
	}

	private void startRecorder()
	{
		playButton.setEnabled(false);
		binder.record(getFileName());
		recordButton.setText(R.string.stop);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == SELECT_FILE && selectedFile != null)
		{
			startPlayer();
		}
		if(requestCode == SELECT_RECORD_FILE && selectedFile != null)
		{
			startRecorder();
		}
	}
    
	private void onPlayClick()
	{
		try
		{
			if(binder.getStatus().getState() == GPSControlServiceState.PLAYING)
			{
				binder.stop();
				playButton.setText("Play");
				recordButton.setEnabled(true);
			}
			else
			{
				selectedFile = null;
				Intent intent = new Intent(this, PlayFileSelector.class);
				startActivityForResult(intent, SELECT_FILE);
			}
		}
		catch(Exception ex)
		{
			GPSControlExceptionHandler.showException(ex);
		}
	}

	private void startPlayer()
	{
		recordButton.setEnabled(false);
		binder.play(getFileName());
		playButton.setText("Stop");
	}
	
	private String getFileName()
	{
		return selectedFile;
	}
	
	private void update(GPSControlServiceStatus status)
	{
		Location location = status.getLocation();
		try
		{
			if(location == null)
			{
				langTextView.setText(GPSControlParams.NA);
				longTextView.setText(GPSControlParams.NA);
				altTextView.setText(GPSControlParams.NA);
				speedTextView.setText(GPSControlParams.NA);
				accuracyTextView.setText(GPSControlParams.NA);
				bearingTextView.setText(GPSControlParams.NA);
				providerTextView.setText(GPSControlParams.NA);
			}
			else
			{
				langTextView.setText(String.valueOf(location.getLatitude()));
				longTextView.setText(String.valueOf(location.getLongitude()));
				altTextView.setText(location.hasAltitude() ? String.valueOf(location.getAltitude()) : GPSControlParams.NA);
				speedTextView.setText(location.hasSpeed() ? String.valueOf(location.getSpeed()) : GPSControlParams.NA);
				accuracyTextView.setText(location.hasAccuracy() ? String.valueOf(location.getAccuracy()) : GPSControlParams.NA);
				bearingTextView.setText(location.hasBearing() ? String.valueOf(location.getBearing()) : GPSControlParams.NA);
				providerTextView.setText(location.getProvider());
			}
			timeTextView.setText(String.valueOf(status.getTimePassed()));
			stateTextView.setText(status.getState().getState());
		}
		catch(Exception ex)
		{
			GPSControlExceptionHandler.showException(ex);
		}
	}
	
	private void refresh()
	{
		updateHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if(binder != null) update(binder.getStatus());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.app_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.MenuExit:
				exitApplication();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void exitApplication()
	{
		finish();
	}
	
	private class LocationPresenter extends AbstractLocationWorker
	{

		public LocationPresenter()
		{
			super("LocationPresenter");
		}

		@Override
		protected void startSpecific() {}

		@Override
		protected void stopSpecific() {}

		@Override
		protected boolean doWork()
		{
			refresh();
			return true;
		}
		
	}
	
}