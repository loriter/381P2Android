package com.camera.simplemjpeg;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MjpegActivity extends Activity {
	private static final boolean DEBUG=false;
    private static final String TAG = "MJPEG";

    private MjpegView mv = null;
    private String mHostName = "IPTHING";
    private int mPort = 5000;

    BluetoothSocket btSocket;
    BluetoothDevice btDevice = null;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        mv = (MjpegView) findViewById(R.id.mv);
        final Button up = (Button) findViewById(R.id.buttonUp);
        final Button down = (Button) findViewById(R.id.buttonDown);
        final Button right = (Button) findViewById(R.id.buttonRight);
        final Button left = (Button) findViewById(R.id.buttonLeft);

        up.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                //TODO Up listener
                return true;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                //TODO Up listener
                return true;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                //TODO Up listener
                return true;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                //TODO Up listener
                return true;
            }
        });
        
        // receive parameters from PreferenceActivity
        Bundle bundle = getIntent().getExtras();
        String hostname = bundle.getString( PreferenceActivity.KEY_HOSTNAME);
        String portnum =  bundle.getString( PreferenceActivity.KEY_PORTNUM);
        new DoRead().execute( hostname, portnum);
    }

    
    public void onResume() {
    	if(DEBUG) Log.d(TAG,"onResume()");
        super.onResume();
        if(mv!=null){
        	mv.resumePlayback();
        }

    }

    public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }
    public void onPause() {
    	if(DEBUG) Log.d(TAG,"onPause()");
        super.onPause();
        if(mv!=null){
        	mv.stopPlayback();
        }
    }
    public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }

    public void onDestroy() {
    	if(DEBUG) Log.d(TAG,"onDestroy()");
    	
    	if(mv!=null){
    		mv.freeCameraMemory();
    	}
    	
        super.onDestroy();
    }
    
    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
    	protected MjpegInputStream doInBackground( String... params){
    		Socket socket = null;
    		try {
				socket = new Socket( params[0], Integer.valueOf( params[1]));
	    		return (new MjpegInputStream(socket.getInputStream()));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return null;
    	}
    	
        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            if(result!=null) result.setSkip(1);
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(true);
        }
    }
}