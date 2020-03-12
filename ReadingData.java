package com.wattathlon.wattathlon2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadingData extends BLEconnection {

    TextView timerTxt, wattsTxt;
    Button startStopBtn;
    ImageButton changeErgBtn;
    ImageView image;

    private boolean start = true; //timer's running
    private volatile boolean threadRunning = false; //thread's running
    private int watts = 0;
    private long startTime = 0;
    private int ftp;

    private static final String TAG = "ReadingData";

    Handler timeHandler = new Handler();
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;

            int seconds = (int) millis / 1000;
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTxt.setText(String.format("%d:%02d", minutes, seconds));
            timeHandler.postDelayed(this, 500);
        }
    };

    //Broadcast Receiver for receiving the new value of power.
    private final BroadcastReceiver PowerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals("POWER")) {

                watts = intent.getIntExtra("NEW_VALUE", 0);
                Log.d(TAG, "power = " + watts) ;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HeartRateZones zones = new HeartRateZones(ftp);

                        final int percentage =  (watts * 100) / ftp;

                        Log.d(TAG,"Percentage: " + percentage);

                        wattsTxt.setText("" + percentage + "%");
                        setImageName(percentage);

                        if (zones.activeRecovery(watts)) {
                            //white zone
                            Log.d(TAG, "white zone");
                        }
                        else if (zones.endurance(watts)) {
                        //blue
                            Log.d(TAG, "blue zone");
                        }
                        else if (zones.tempo(watts)) {
                        //green
                            Log.d(TAG, "green zone");
                        }
                        else if (zones.threshold(watts)) {
                        //yellow
                            Log.d(TAG, "yellow zone");
                        }
                        else if (zones.vo2max(watts)) {
                        //red
                            Log.d(TAG, "red zone");
                        }
                        else if (zones.anaerobicCapacity(watts)) {
                        //purple
                            Log.d(TAG, "purple zone");
                        }
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Make secondCall flag true,
        secondCall = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_data);

        image = (ImageView) findViewById(R.id.image);
        wattsTxt = (TextView) findViewById(R.id.wattsTxt);
        timerTxt = (TextView) findViewById((R.id.timerTxt));
        startStopBtn = (Button) findViewById(R.id.startStopBtn);
        changeErgBtn = (ImageButton) findViewById(R.id.changeErgBtn);

        changeErgBtn.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);

        switch (((Account) getApplication()).getErgType()) {
            case "row": ftp = ((Account) getApplication()).getRowFtp();
                        break;
            case "bike": ftp = ((Account) getApplication()).getBikeFtp();
                        break;
            case "ski": ftp = ((Account) getApplication()).getSkiFtp();
                        break;
        }

        registerReceiver(PowerReceiver, new IntentFilter("POWER"));

        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!start) {
                    Log.d(TAG, "Timer's stopped");

                    timeHandler.removeCallbacks(timeRunnable);

                    wattsTxt.setVisibility(View.INVISIBLE);
                    image.setVisibility(View.INVISIBLE);

                    startStopBtn.setText("Start");
                    changeErgBtn.setVisibility(View.VISIBLE);

                    start = true;
                    threadRunning = false;
                }
                else { //button's text is "stop" and timer is running
                    Log.d(TAG, "Timer's running");
                    //wattsTxt.setVisibility(View.VISIBLE);

                    startTime = System.currentTimeMillis();
                    timeHandler.postDelayed(timeRunnable, 0);

                    startStopBtn.setText("Stop");
                    changeErgBtn.setVisibility(View.INVISIBLE);

                    start = false;
                }
            }
        });

        changeErgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeErg = new Intent(getApplicationContext(), Choose.class);
                startActivity(changeErg);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        timeHandler.removeCallbacks(timeRunnable);
        startStopBtn = (Button) findViewById((R.id.startStopBtn));
        startStopBtn.setText("Start");
        start = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(PowerReceiver);
//        unregisterReceiver(GattReceiver);
    }

    //Method that sets the correct image according to the percentage value.
    public void setImageName(int percentage) {
        wattsTxt.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
        if(percentage <= 5) {
            image.setImageResource(R.drawable.ic_1);
        }
        else if(percentage > 5 && percentage <= 10) {
            image.setImageResource(R.drawable.ic_2);
        }
        else if(percentage > 10 && percentage <= 15) {
            image.setImageResource(R.drawable.ic_3);
        }
        else if(percentage > 15 && percentage <= 20) {
            image.setImageResource(R.drawable.ic_4);
        }
        else if(percentage > 20 && percentage <= 25) {
            image.setImageResource(R.drawable.ic_5);
        }
        else if(percentage > 25 && percentage <= 30) {
            image.setImageResource(R.drawable.ic_6);
        }
        else if(percentage > 30 && percentage <= 35) {
            image.setImageResource(R.drawable.ic_7);
        }
        else if(percentage > 35 && percentage <= 40) {
            image.setImageResource(R.drawable.ic_8);
        }
        else if(percentage > 40 && percentage <= 45) {
            image.setImageResource(R.drawable.ic_9);
        }
        else if(percentage > 45 && percentage <= 50) {
            image.setImageResource(R.drawable.ic_10);
        }
        else if(percentage > 50 && percentage <= 55) {
            image.setImageResource(R.drawable.ic_11);
        }
        else if(percentage > 55 && percentage <= 60) {
            image.setImageResource(R.drawable.ic_12);
        }
        else if(percentage > 60 && percentage <= 65) {
            image.setImageResource(R.drawable.ic_13);
        }
        else if(percentage > 65 && percentage <= 70) {
            image.setImageResource(R.drawable.ic_14);
        }
        else if(percentage > 70 && percentage <= 75) {
            image.setImageResource(R.drawable.ic_15);
        }
        else if(percentage > 75 && percentage <= 80) {
            image.setImageResource(R.drawable.ic_16);
        }
        else if(percentage > 80 && percentage <= 85) {
            image.setImageResource(R.drawable.ic_17);
        }
        else if(percentage > 85 && percentage <= 90) {
            image.setImageResource(R.drawable.ic_18);
        }
        else if(percentage > 90 && percentage <= 95) {
            image.setImageResource(R.drawable.ic_19);
        }
        else if(percentage > 95 && percentage <= 100) {
            image.setImageResource(R.drawable.ic_20);
        }
        else if(percentage > 100 && percentage <= 105) {
            image.setImageResource(R.drawable.ic_21);
        }
        else if(percentage > 105 && percentage <= 110) {
            image.setImageResource(R.drawable.ic_22);
        }
        else if(percentage > 110 && percentage <= 115) {
            image.setImageResource(R.drawable.ic_23);
        }
        else if(percentage > 115 && percentage <= 120) {
            image.setImageResource(R.drawable.ic_24);
        }
        else {
            image.setImageResource(R.drawable.ic_max);
        }
    }
}
