package org.nambar.gpscontrol;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecordFileSelector extends Activity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_selection);

		Button okButton = (Button)findViewById(R.id.ButtonOK);
		okButton.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		TextView view = (TextView)findViewById(R.id.NewFileText);
        		GPSControlActivity.selectedFile = view.getText().length() > 0 ? Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + GPSControlParams.FILES_LOCATION + File.separator + view.getText().toString() : null;
        		finish();
        	}
        });
}
}
