package org.nambar.gpscontrol;

import java.io.File;
import java.util.Arrays;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PlayFileSelector extends ListActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, getItems()));
        final ListView listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
    	GPSControlActivity.selectedFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + GPSControlParams.FILES_LOCATION + File.separator +  (String)l.getItemAtPosition(position);
    	finish();
    }
    

    private String[] getItems()
    {
    	String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + GPSControlParams.FILES_LOCATION;
    	String[] files = (new File(path)).list(); 
    	Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
    	return files;
	}

}

