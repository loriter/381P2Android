package com.camera.simplemjpeg;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final boolean DEBUG=false;
    private static final String TAG = "Video";

    BluetoothSocket btSocket;
    BluetoothDevice btDevice = null;
    BluetoothAdapter btAdapter;
    OutputStream btOutputStream;
    
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        final Button up = (Button) findViewById(R.id.buttonUp);
        final Button down = (Button) findViewById(R.id.buttonDown);
        final Button right = (Button) findViewById(R.id.buttonRight);
        final Button left = (Button) findViewById(R.id.buttonLeft);
        final WebView wv = (WebView) findViewById(R.id.webstream);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        final class workerThread implements Runnable {
            private String btMsg;

            public workerThread(String msg) {
                btMsg = msg;
            }

            public void run() {
                sendBTMsg(btMsg);
            }
        }

        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                Log.i("Up", "Motion");

                switch(e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("Up", "Pressed");
                        (new Thread(new workerThread("u"))).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        (new Thread(new workerThread("nu"))).start();
                        Log.i("Up", "Let go");
                        break;
                }
                return true;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch(e.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        (new Thread(new workerThread("d"))).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        (new Thread(new workerThread("nd"))).start();
                        break;
                }
                return true;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch(e.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        sendBTMsg("r");
                        break;
                    case MotionEvent.ACTION_UP:
                        sendBTMsg("nr");
                        break;
                }
                return true;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch(e.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        sendBTMsg("l");
                        break;
                    case MotionEvent.ACTION_UP:
                        sendBTMsg("nl");
                        break;
                }
                return true;
            }
        });

        if(!btAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevice = btAdapter.getBondedDevices();
        if(pairedDevice.size() > 0) {
            for(BluetoothDevice device : pairedDevice) {
                if(device.getName().equals("PiCar")) {
                    Log.i(TAG, device + " connected.");
                    btDevice = device;
                    break;
                }
            }
        }

        //connectBT();

        // receive parameters from PreferenceActivity
        Bundle bundle = getIntent().getExtras();
        String hostname = bundle.getString( PreferenceActivity.KEY_HOSTNAME);
        String portnum =  bundle.getString( PreferenceActivity.KEY_PORTNUM);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.loadUrl("http://" + hostname + ":" + portnum);
    }

    
    public void onResume() {
    	if(DEBUG) Log.d(TAG,"onResume()");
        super.onResume();
        connectBT();
    }

    public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }

    public void onRestart() {
        if(DEBUG) Log.d(TAG,"onRestart");
        super.onRestart();
    }

    public void onPause() {
    	if(DEBUG) Log.d(TAG,"onPause()");
        super.onPause();
        sendBTMsg("rc");
        try {
            btSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }

    public void onDestroy() {
    	if(DEBUG) Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void sendBTMsg(String msg) {
        Log.i("sendBT", msg);
        try {
            btOutputStream = btSocket.getOutputStream();
            btOutputStream.write(msg.getBytes());
            Log.i("Byte sent", "" + msg.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void connectBT() {
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
        try {
            btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
            while(!btSocket.isConnected()) {
                btSocket.connect();
            }
            btOutputStream = btSocket.getOutputStream();
            Log.i("Socket", "Connected");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}