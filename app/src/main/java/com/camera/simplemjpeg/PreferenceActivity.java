package com.camera.simplemjpeg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PreferenceActivity extends Activity {
	public static final String KEY_HOSTNAME = "hostname";
	public static final String KEY_PORTNUM = "portnum";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.setContentView( R.layout.preference);
		
		// load stored data
		SharedPreferences sp = this.getPreferences( MODE_PRIVATE);
		String hostname = sp.getString( KEY_HOSTNAME, this.getString( R.string.defaultHostName));
		String portnum = sp.getString( KEY_PORTNUM, this.getString( R.string.defaultPortNum));
		
		// set stored parameters to the contents
		EditText et = (EditText)findViewById( R.id.editText_hostname);
		et.setText( hostname);
		et = (EditText)findViewById( R.id.editText_portnum);
		et.setText( portnum);
	}

	/*
	 * This function is called when user clicks start button.
	 */
	public void onClick( View view){
		// get data from EditText components
		EditText etHost = (EditText)findViewById( R.id.editText_hostname);
		EditText etPort = (EditText)findViewById( R.id.editText_portnum);
		String hostname = etHost.getText().toString();
		String portnum = etPort.getText().toString();
		
		// store the input data
		SharedPreferences sp = this.getPreferences( MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString( KEY_HOSTNAME, hostname);
		editor.putString( KEY_PORTNUM, portnum);
		editor.commit();
		
		// launch MainActivity
		Intent intent = new Intent( this, MainActivity.class);
		intent.putExtra( KEY_HOSTNAME, hostname);
		intent.putExtra( KEY_PORTNUM, portnum);
		this.startActivity( intent);
	}
}
