package com.example.io_detection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    //PrintWriter writer = null;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    //File appRootFile;
    File currentFile;
    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
  //  private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ArrayAdapter arrayAdapter;
    private int Best_rssi = -1;
    private int Avg_rssi = 0;

    private int firstst_ap = 0;
    private int second_ap = 0;
    private int screen_width;
    private int screen_height;
    private float GPSSnrUpperThreshold = 0f;
    private float GPSSnrMiddleThreshold = 0f;
    private float GPSSnrLowerThreshold = 0f;
    private float GPSCountUpperThreshold = 0f;
    private float GPSCountMiddleThreshold = 0f;
    private float GPSCountLowerThreshold = 0f;

    private float indoorConfidence = 0f;
    private float semioutdoorConfidence = 0f;
    private float outdoorConfidence = 0f;



    private SensorManager IOSensorManager;
    private Sensor IOProximity;
    private Boolean ProximitySensorAvailable = false;
    private float ProximityValue;
    private Calendar cal = Calendar.getInstance();
    private Sensor IOLight;
    private Boolean LightSensorAvailable = false;
    private float LightValue;
    private Sensor IOMagnetism;
    private Boolean MagnetismSensorAvailable = false;
    private float MagnetismValue;

    private int round_count = 0;
    private float[] GPSSnrValue = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private float[] GPSSnrTrend = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private float GPSSnrTrendMax = 0.0f;
    private float[] magnetismStrength = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private float magnetismVariation =0f;
    private Sensor IOAccelerometer;
    private Boolean AccelerometerSensorAvailable = false;
    private float AccelerometerValue;
    private float[] AccelerometerStrength = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private float AccelerometerVariation = 0f;
    private static final String TAG = "Gnss demo";
    GnssStatus.Callback mGnssCallback;
    int mSatelliteCount;
    boolean mSatFixCount;
    private int mUsedInFix;
    LocationManager mLocationManager;
    Float mSignal;
    TextView tt1, tt4, tt7;
    String st1, st5, st6, st7, st8, st9, st10 = "Detected Environment:";
    TextView status, GnssLightStatus,MagStatus,LightStatus,AccStatus,SnrStatus,CountStatus,GpstheseholdStatus,Cell;

    private boolean firstRound = true;
    private boolean clickOnce = true;
    private boolean apk = true;
    //CSVWriter csvWriter;
   // File file;
   // String[] data;
   // String[] name;
    public String filestring="";
    public String wstring="";
    public String Result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        View view = this.getWindow().getDecorView();
//        view.setBackgroundColor(Color.BLUE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation

        setProperThreshold();



        //fileString = "";


        buttonScan = findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            }, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                }
                else {
                    scanWifi();
                }


            }
        });
        MagStatus= findViewById(R.id.mag);
        LightStatus = findViewById(R.id.light);
        AccStatus = findViewById(R.id.Acc);
        listView = findViewById(R.id.lv);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
            Best_rssi=-1;
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        scanWifi();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mGnssCallback = new GnssStatus.Callback() {

            @Override
            public void onStarted() {

                super.onStarted();
            }

            @Override
            public void onStopped() {
                super.onStopped();
            }

            @Override
            public void onFirstFix(int ttffMillis) {
                super.onFirstFix(ttffMillis);
            }

            @SuppressLint("ResourceType")
            @Override
            public void onSatelliteStatusChanged(GnssStatus status) {
                mUsedInFix = 0;

                // get available satellite count and print
                mSatelliteCount = status.getSatelliteCount();
                Log.i(TAG, "All Available satellites: " + mSatelliteCount);


                for (int i = 0; i < mSatelliteCount; i++) {
                    //get signal & print
                    mSignal = status.getCn0DbHz(i);
                    String signalst1 = "Signal Strength: " + mSignal + " dBm";
                    tt4 = (TextView) findViewById(R.id.textView1);
                    tt4.setText(signalst1);


                    //get used in fix
                    mSatFixCount = status.usedInFix(i);
                    if (mSatFixCount == true) {
                        mUsedInFix++;
                    }

                    String Satst4 = "Used Satellites :" + mUsedInFix;
                    tt7 = (TextView) findViewById(R.id.textView2);
                    tt7.setText(Satst4);

                    //check indoor/outdoor status
                    tt1 = (TextView) findViewById(R.id.textView3);

                    if (mSignal >= 0 && mSignal <= 19 && mUsedInFix<25) {
                        st7 = "Indoor";
                        String st11 = st10 + st7;
                        tt1.setText(st11);
                        if ((st6 == st7) || (st7.equalsIgnoreCase("Indoor"))  ) {
                            GnssLightStatus = (TextView) findViewById(R.id.textView4);
                            GnssLightStatus.setText("you are Indoor");
                        } else {
                            GnssLightStatus.setText("Poor Confidence");
                        }
                    } else if (mSignal >= 20 && mSignal <= 31 && mUsedInFix>25 && mUsedInFix<30 ) {

                        st8 = "Semi outdoor";
                        String st12 = st10 + st8;
                        tt1.setText(st12);
                        if (st8 == st5 || st8.equalsIgnoreCase("Semi outdoor")) {
                            GnssLightStatus = (TextView) findViewById(R.id.textView4);
                            GnssLightStatus.setText("you are Semi outdoor");
                        } else {
                            GnssLightStatus.setText("Poor Confidence");
                        }

                    } else {

                        st9 = "Outdoor";
                        String st15 = st10 + st9;
                        tt1.setText(st15);
                        if (st9 == st1 || st9.equalsIgnoreCase("Outdoor") && mUsedInFix>30 ) {
                            GnssLightStatus = (TextView) findViewById(R.id.textView4);
                            GnssLightStatus.setText("you are Outdoor");
                        } else {
                            GnssLightStatus.setText("Poor Confidence");
                        }

                    }
                }

                super.onSatelliteStatusChanged(status);
            }

        };


        IOSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //list all available sensors
        List<Sensor> IOList = IOSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : IOList) {
            if (sensor.getType() == Sensor.TYPE_LIGHT) {
                LightSensorAvailable = true;
            }
            if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
                ProximitySensorAvailable = true;
            }
            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                MagnetismSensorAvailable = true;
            }
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                AccelerometerSensorAvailable = true;
            }
        }
        if (LightSensorAvailable) {
            IOLight = IOSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            IOSensorManager.registerListener(this, IOLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (ProximitySensorAvailable) {
            IOProximity = IOSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            IOSensorManager.registerListener(this, IOProximity, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (MagnetismSensorAvailable) {
            IOMagnetism = IOSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            IOSensorManager.registerListener(this, IOMagnetism, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (AccelerometerSensorAvailable) {
            IOAccelerometer = IOSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            IOSensorManager.registerListener(this, IOAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }


        SnrStatus= findViewById(R.id.snr);
        CountStatus = findViewById(R.id.count);
        GpstheseholdStatus= findViewById(R.id.gpsthesehold);
        Cell= findViewById(R.id.cc);



    }


    public void setProperThreshold(){

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width = size.x; //width
        screen_height = size.y; //height

        if(screen_width < 1200) { // 1080
            GPSSnrUpperThreshold = 23;
            GPSSnrMiddleThreshold = 18;
            GPSSnrLowerThreshold = 15;
        }
        else { // 1440
            GPSSnrUpperThreshold = 25;
            GPSSnrMiddleThreshold = 22;
            GPSSnrLowerThreshold = 19;
        }

        GPSCountUpperThreshold = 4.5f;
        GPSCountMiddleThreshold = 3.5f;
        GPSCountLowerThreshold = 1.5f;
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            ProximityValue = event.values[0];
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            LightValue = event.values[0];
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            MagnetismValue = (float) Math.sqrt(event.values[0]*event.values[0] + event.values[1] * event.values[1] + event.values[2]*event.values[2]);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
           AccelerometerValue = (float) Math.sqrt(event.values[0]*event.values[0] + event.values[1] * event.values[1] + event.values[2]*event.values[2]);
        }
        String Lux = "Light sensor: " + LightValue+ " lux";
        LightStatus.setText(Lux);
        String acc = "Accelerometer: " + AccelerometerValue+ " mv/g";
        AccStatus.setText(acc);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    private void scanWifi() {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    protected void onPause(){
        super.onPause();
        //unregisterReceiver(wifiReceiver);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                    wifiManager.startScan();
                } else {
                    Toast.makeText(MainActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }


    }
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            int i=0,j=0;
            results = wifiManager.getScanResults();
            if(results.size()==0){
                Toast.makeText(MainActivity.this, "No wifi found!", Toast.LENGTH_SHORT).show();

            }
            else{
            Best_rssi=results.get(0).level;}
            unregisterReceiver(this);
            arrayList.add("No of wifi AP:"+results.size());
            for (ScanResult scanResult : results) {
                arrayList.add("SSID:"+scanResult.SSID +",BSSID:"+scanResult.BSSID+",Capabilities:"+ scanResult.capabilities+",Level:"+scanResult.level );
                Avg_rssi+=scanResult.level;
                if (Best_rssi<scanResult.level) {

                    Best_rssi = scanResult.level;

                }
                if(scanResult.level>-67){
                    i++;
                } else if(scanResult.level<-67 && scanResult.level>-80){
                    j++;
                }


                //adapter.notifyDataSetChanged();
            }
            //arrayList.add("rssi:"+Best_rssi);
            Avg_rssi/=results.size();
            firstst_ap=i;
            second_ap=j;
            //Toast.makeText(MainActivity.this,  Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "rssi:"+Best_rssi+"," +"avgrssi:"+Avg_rssi+","+"1st:"+firstst_ap+","+"2nd:"+second_ap, Toast.LENGTH_SHORT).show();
            arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, results);
            listView.setAdapter(arrayAdapter);
        }

    };


    public LocationManager manager;

    public void initLocation() {

        {
           /* try {
                //csvWriter = new CSVWriter(new FileWriter(file));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            //name[0]="SNR";
           // name[1]="COUNT";
            //name[2]="RSSI";
            //name[3]="LIGHT";
            //name[04]="MAGNETOMETER";
           // csvWriter.writeNext(name);
        }
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please open GPS service", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location permission required")
                        .setMessage("You have to give the permission to access location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


            }
        } else {
            manager.addGpsStatusListener(gpsStatusListener);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            mLocationManager.registerGnssStatusCallback(mGnssCallback);
            GnssLightStatus=(TextView)findViewById(R.id.textView4);
        }
    }

    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                return;
            }
            GpsStatus gpsStatus = manager.getGpsStatus(null);
            int maxSatellites = gpsStatus.getMaxSatellites();
            Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
            int count_gps = 0;
            int count_gln = 0;
            float snr_gps = 0;
            float snr_gln = 0;
            float avg_snr_gps;
            float avg_snr_gln;

            StringBuilder sb = new StringBuilder();
            StringBuilder satelliteInfo = new StringBuilder();
            System.out.println("found satellites === " + iters.hasNext());
            while (iters.hasNext() && count_gps <= maxSatellites) {
                GpsSatellite s = iters.next();
                int prn = s.getPrn();
                float snr = s.getSnr();
                if (snr > 0.0){
                    if (prn < 33){ // gps 1-32
                        count_gps++;
                        snr_gps += snr;
                    }
                    if ((prn > 64 && prn < 89)||(prn > 37 && prn < 62)){ // gln 65-88 and 38-61
                        count_gln++;
                        snr_gln += snr;
                    }
                }
            }
            avg_snr_gps = snr_gps/count_gps;
            avg_snr_gln = snr_gln/count_gln;

            //the GPS snr in past 30 secs
            for(int i=0; i<29; i++) {
                GPSSnrValue[i] = GPSSnrValue[i+1];
            }
            GPSSnrValue[29] = avg_snr_gps;

            //detection round count
            round_count++;
            //the first 30-sec round ends
            if(round_count == 30){
                firstRound = false;
            }

            if(!firstRound){
                for(int i=0; i<20; i++)
                    GPSSnrTrend[i] = GPSSnrValue[i] - GPSSnrValue[i+9];

                //snr variation in past 30 secs
                GPSSnrTrendMax = GPSSnrTrend[0];
                for (int i = 1; i < GPSSnrTrend.length; i++) {
                    if (GPSSnrTrend[i] > GPSSnrTrendMax) {
                        GPSSnrTrendMax = GPSSnrTrend[i];
                    }
                }
            }
            String Snrstring = "Gps SNR: " + avg_snr_gps;
            SnrStatus.setText(Snrstring);
            String Countstring = "Gps Count: " + count_gps;
            CountStatus.setText(Countstring);
            String Gpstheseholdstring = "Gps Count: " +GPSSnrTrendMax ;
            GpstheseholdStatus.setText(Gpstheseholdstring);



            IODetection(count_gps, avg_snr_gps);
        }
    };

    public void IODetection(int count, float snr) {
        TextView statusNow = findViewById(R.id.showResult);
        List<String[]>data1=new ArrayList<String[]>();


      /*  try {
            data[0]=Float.toString(snr);
            data[1]=Integer.toString(count);

            data[2]=Float.toString(Best_rssi);
            data[3]=Float.toString(LightValue);
            data[4]=Float.toString(magnetismVariation);

            csvWriter.writeNext(data);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }*/

        getConfidenceLevelFromSatellites(count, snr);
       // getConfidenceLevelFromCellular();
        getConfidenceLevelFromMagneticField();
        wificonfidencelevel();
        /*if(mUsedInFix>0){
            getConfidenceLevelFromGnss();
        }*/


        if((outdoorConfidence > indoorConfidence) && (outdoorConfidence > semioutdoorConfidence)){
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#2cb457")); //outdoor
            statusNow.setText("Detection result: outdoor");
            Result="outdoor";

            if(apk){
                Intent LaunchIntent=getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                if(LaunchIntent!=null){
                    startActivity(LaunchIntent);
                }
                else{
                    Toast.makeText(MainActivity.this,"There is no package availale in android",Toast.LENGTH_LONG);
                }
                apk=false;
            }
        }else if((semioutdoorConfidence > indoorConfidence) &&(semioutdoorConfidence > outdoorConfidence)){
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#ffce26")); // semi
            statusNow.setText("Detection result: semi-outdoor");
            Result="semiutdoor";
        }else if((indoorConfidence > outdoorConfidence)&&(indoorConfidence > semioutdoorConfidence)) {
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#ff6714")); //indoor
            statusNow.setText("Detection result: indoor");
            Result="indoor";

            if (apk){
                Intent LaunchIntent=getPackageManager().getLaunchIntentForPackage("org.test.myapp");
                if(LaunchIntent!=null){
                    startActivity(LaunchIntent);
                }
                else{
                    Toast.makeText(MainActivity.this,"There is no package availale in android",Toast.LENGTH_LONG);
                }
                apk=false;
            }
        }else if(indoorConfidence == outdoorConfidence && indoorConfidence == semioutdoorConfidence){
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#00c3e3")); //unknown
            statusNow.setText("Detection result: unknown");
        }
        filestring=filestring+snr+", "+count+", "+GPSSnrTrendMax+", "+results.size()+","+Best_rssi+","+Avg_rssi+","+firstst_ap+","+second_ap+","+LightValue+", "+MagnetismValue+", "+magnetismVariation+", "+ProximityValue+", "+AccelerometerValue+", "+Result+"\n";
        //filestring = filestring + sX + ", " + sY + ", " + sZ + "\n";
        FileWriters(filestring);
        filestring="";
        indoorConfidence = 0f;
        semioutdoorConfidence = 0f;
        outdoorConfidence = 0f;
    }
    private void getConfidenceLevelFromGnss(){
        if (cal.get(Calendar.HOUR_OF_DAY) > 19 && cal.get(Calendar.HOUR_OF_DAY) < 22) {

            if (mSignal >= 0 && mSignal <= 19 && mUsedInFix < 25) {
                if ((st6 == st7) || (st7.equalsIgnoreCase("Indoor"))) {
                    indoorConfidence = indoorConfidence + 10;

                }
            } else if (mSignal >= 20 && mSignal <= 31 && mUsedInFix >= 25 && mUsedInFix <= 30) {

                if (st8 == st5 || st8.equalsIgnoreCase("Semi outdoor")) {
                    semioutdoorConfidence = semioutdoorConfidence + 3;
                } else {
                    GnssLightStatus.setText("Poor Confidence");
                }

            } else {
                if (st9 == st1 || st9.equalsIgnoreCase("Outdoor")) {
                    outdoorConfidence = outdoorConfidence + 10;
                } else {
                    GnssLightStatus.setText("Poor Confidence");
                }

            }

        }
    }
    private void getConfidenceLevelFromSatellites(int count, float snr){


        if(ProximityValue > 3){
            if(LightValue > 3000 && count > GPSCountMiddleThreshold){
                outdoorConfidence = outdoorConfidence + 10;
            }
            else {
                if (snr > GPSSnrUpperThreshold) {//snr>23
                    if (count > GPSCountMiddleThreshold ) {
                        outdoorConfidence = outdoorConfidence + 9;
                    } else if(count > GPSCountLowerThreshold){
                        semioutdoorConfidence = semioutdoorConfidence + 8;//
                    } else {
                        indoorConfidence = indoorConfidence + 9;
                    }
                } else {
                    if (snr > GPSSnrMiddleThreshold) {
                        if (count > GPSCountMiddleThreshold) {
                            if (mUsedInFix>0 && mUsedInFix <25) {
                                indoorConfidence = indoorConfidence + 9;
                            } else {
                                semioutdoorConfidence = semioutdoorConfidence + 8;//
                            }
                        } else {
                            indoorConfidence = indoorConfidence + 8;
                        }
                    } else {
                        if (count < GPSCountUpperThreshold || snr < GPSSnrLowerThreshold) {
                            indoorConfidence = indoorConfidence + 10;
                        } else {
                            if (cal.get(Calendar.HOUR_OF_DAY) > 9 && cal.get(Calendar.HOUR_OF_DAY) < 17) { //daytime
                                if (LightValue < 1500) {
                                    indoorConfidence = indoorConfidence + 9;
                                }
                            } else if (GPSSnrTrendMax > 6.5) {
                                indoorConfidence = indoorConfidence + 7;
                            }
                        }
                    }
                }
            }
        }
        else{
            if(count > 4.5 && snr > (GPSCountUpperThreshold -2)){
                outdoorConfidence = outdoorConfidence + 9;
            }
            if(count > 4.5 && GPSSnrTrendMax>6.5 && (snr < GPSCountMiddleThreshold-2)){
                indoorConfidence = indoorConfidence + 7;
            }
            if(count > 2.5 && (snr > GPSSnrMiddleThreshold-2 ) && snr < (GPSCountUpperThreshold-2)){
                semioutdoorConfidence = semioutdoorConfidence + 7;
            }
            if(count <2.5 && (snr < GPSCountLowerThreshold-2)){
                indoorConfidence = indoorConfidence + 9;
            }
            if(count <0.5){
                indoorConfidence = indoorConfidence + 10;
            }
        }
    }
    //wifi part
    public void wificonfidencelevel() {
        //TextView statusNow = findViewById(R.id.showResult);
        status = (TextView) findViewById(R.id.statuslight);

       // Log.i("rssi", "deMaxrssi：" + max_rssi);
        //Toast.makeText(this,"accuracy"+accuracy , Toast.LENGTH_SHORT).show();


        if ( Best_rssi > -50 ) {
            indoorConfidence = indoorConfidence + 10;
        }
        else if(firstst_ap>1 && Avg_rssi<=-80 && second_ap>3){
            indoorConfidence = indoorConfidence + 7;
        }
       // else if( results.size()>15 && Avg_rssi<= -85){
       //     outdoorConfidence = outdoorConfidence + 8;
       // }



            if (cal.get(Calendar.HOUR_OF_DAY) > 9 && cal.get(Calendar.HOUR_OF_DAY) < 17) {

                //daytime
                if (LightValue > 0 && LightValue <= 500)
                {
                    st6 ="Indoor";
                    String st14;
                    st14=st10.concat(st6);
                    status.setText(st14);
                    indoorConfidence=indoorConfidence+10;

                }
                else if (LightValue > 500 && LightValue < 2000)
                {
                    st5 ="Semi outdoor";
                    String st13;
                    st13=st10+st5;
                    status.setText(st13);
                    semioutdoorConfidence=semioutdoorConfidence+3;
                }
                else if (LightValue > 2000)
                {
                    st1 ="Outdoor";
                    String st13;
                    st13=st10+st1;
                    status.setText(st13);
                    outdoorConfidence=outdoorConfidence+10;

                }
                else
                {
                    status.setText("Detected Environment: Light sensor ineffective");
                }
            }



           /* else  {

                if(LightValue<=10){

                    if(AccelerometerValue>1.3){

                        outdoorConfidence=outdoorConfidence+2;
                    }

                } else if (LightValue>=10 && LightValue<=900){
                    indoorConfidence=indoorConfidence+9;
                }

            }*/


    }


    private void getConfidenceLevelFromMagneticField(){
        boolean magnetismAvailable = true;
        for(int i=0; i<9;i++){
            magnetismStrength[i] = magnetismStrength[i+1];
        }
        magnetismStrength[9] = MagnetismValue;
        if(magnetismStrength[0] == 0f){
            magnetismAvailable = false;
        }else{
        magnetismVariation = varianceImperative(magnetismStrength);
        String magV = "Magnetometer Sensor: " + magnetismVariation+ " micro Telsa";
        MagStatus.setText(magV);
        if(magnetismVariation > 150)
            indoorConfidence = indoorConfidence + 3;
        }
    }

    public static float varianceImperative(float[] signal) {
        double average = 0.0;
        for (double p : signal) {
            average += p;
        }
        average /= signal.length;

        double variance = 0.0;
        for (double p : signal) {
            variance += (p - average) * (p - average);
        }
        return (float) variance / signal.length;
    }

    public void searchGPSButtonClick(View v) {
        if(clickOnce) {
            clickOnce = false;
            TextView startButton = findViewById(R.id.getStarted);
            startButton.setText("Click to stop");
            TextView detectionResult = findViewById(R.id.showResult);
            detectionResult.setText("Detection result");
            TextView status = findViewById(R.id.status);
            status.setText("Updating every 1 sec");
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#ffffff"));

            File path = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/IO_App");

            if (!path.exists()){
                path.mkdirs();

            }
            Long date1=System.currentTimeMillis();
            SimpleDateFormat dateFormat =new SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.getDefault());
            String dateStr = dateFormat.format(date1);
            System.out.println(dateStr);

            currentFile = new File( path + "/" + dateStr);
            if (!currentFile.exists()) {
                if (!currentFile.mkdirs()) {
                    System.out.println("Root Not Created");
                }
            }
            initLocation();
        }else{
            clickOnce = true;

            finish(); //restart current activity
            startActivity(getIntent());
            TextView startButton = findViewById(R.id.getStarted);
            startButton.setText("Click to start");
            TextView detectionResult = findViewById(R.id.showResult);
            detectionResult.setText("Detection result");
            TextView status = findViewById(R.id.status);
            status.setText("Updating frequency");
        }
    }

    public void FileWriters(String str){
        SimpleDateFormat dateObj = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateObj.format(calendar.getTime());

        //for CSV file


        final File file = new File(currentFile, "IO_data.csv");

        try {
            if(!file.exists()){
                file.createNewFile();
                Toast.makeText(MainActivity.this, "csv created!", Toast.LENGTH_SHORT).show();
                FileOutputStream fOut = new FileOutputStream(file, true);
//                OutputStreamWriter outWriter = new OutputStreamWriter(fOut);
//                outWriter.append("GPS_Lat, GPS_Long, AX, AY, AZ, GX, GY, GZ\n");
//                outWriter.close();
                fOut.write("GPS_SNR, GPS_COUNT, GPS_SNR_TREND_MAX,No_OF_AP,WIFI_Best_RSSI,WIFI_AVg_RSSI,1st_AP,2nd_AP, LIGHT, MAGNETISM, MAG_VERIANCE,Proxmity,Accelerometer,Result\n".getBytes());
//                fOut.flush();
                fOut.close();


            }

            FileOutputStream fOut = new FileOutputStream(file, true);
//            OutputStreamWriter outWriter = new OutputStreamWriter(fOut);
//            outWriter.append(str);
//
//            outWriter.close();
            fOut.write(str.getBytes());
            fOut.flush();
            fOut.close();

        } catch (IOException e){
            Log.e("Exception", "File write failed");
        }

        filestring = "";


//        try {
//            File file = new File(date + ".csv");
//            if (!file.exists()){
//                file.createNewFile();
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("location","latitude and longitude："+location.getLatitude()+"，"+location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };
}














































