package com.example.accelerometersimulation;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final int MAX_DATA_POINTS = 100;
    // UI Components
    TextView tv_acc_x;
    TextView tv_acc_y;
    TextView tv_acc_z;
    TextView tv_acc_x_max;
    TextView tv_acc_y_max;
    TextView tv_acc_z_max;
    Button bt_acc;

    // Max acc values
    private float max_acc_x=0;
    private float max_acc_y=0;
    private float max_acc_z=0;

    // Sensors
    private SensorManager sensorManager;
    private Sensor sensor;

    // Graph
    private GraphView graph;
    private LineGraphSeries<DataPoint> series_x;
    private LineGraphSeries<DataPoint> series_y;
    private LineGraphSeries<DataPoint> series_z;
    private ArrayList<DataPoint> accelXList = new ArrayList<DataPoint>();
    private ArrayList<DataPoint> accelYList = new ArrayList<DataPoint>();;
    private ArrayList<DataPoint> accelZList = new ArrayList<DataPoint>();

    private Boolean acc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // UI Components
        tv_acc_x = findViewById(R.id.tv_acc_x);
        tv_acc_y = findViewById(R.id.tv_acc_y);
        tv_acc_z = findViewById(R.id.tv_acc_z);
        tv_acc_x_max=findViewById(R.id.tv_acc_x_max);
        tv_acc_y_max=findViewById(R.id.tv_acc_y_max);
        tv_acc_z_max=findViewById(R.id.tv_acc_z_max);
        bt_acc = findViewById(R.id.bt_acc);

        bt_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acc) {
                    Toast.makeText(MainActivity.this, "Stopped tracking accelerometer", Toast.LENGTH_LONG).show();
                    acc=false;
                } else {
                    acc=true;
                    Toast.makeText(MainActivity.this, "Tracking accelerometer", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Accelerometer
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        // Graph UI
        graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Sample (n)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Acceleration (m/s^2)");
        graph.setVisibility(View.VISIBLE);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMinY(0);



        series_x=new LineGraphSeries<>(accelXList.toArray(new DataPoint[0]));
        series_y=new LineGraphSeries<>(accelYList.toArray(new DataPoint[0]));
        series_z=new LineGraphSeries<>(accelZList.toArray(new DataPoint[0]));

        // Set series legend
        series_x.setTitle("Acceleration x");
        series_y.setTitle("Acceleration y");
        series_z.setTitle("Acceleration z");

        // Add legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        // Add series to graph
        graph.addSeries( series_x);
        graph.addSeries( series_y);
        graph.addSeries( series_z);

        // Set series colour
        series_x.setColor(Color.RED);
        series_y.setColor(Color.GREEN);
        series_z.setColor(Color.BLUE);

        // Set series animated props
//        series_x.setAnimated(true);
//        series_x.setDrawDataPoints(true);
//        series_y.setAnimated(true);
//        series_y.setDrawDataPoints(true);
//        series_z.setAnimated(true);
//        series_z.setDrawDataPoints(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float acc_x = event.values[0];
        float acc_y = event.values[1];
        float acc_z = event.values[2];

        // Update Max values
        if (acc_x>max_acc_x) max_acc_x = acc_x;
        if (acc_y>max_acc_y) max_acc_y = acc_y;
        if (acc_z>max_acc_z) max_acc_z = acc_z;

        // Add to series
        if(acc) {
            accelXList.add(new DataPoint(accelXList.size(), acc_x));
            accelYList.add(new DataPoint(accelXList.size(), acc_y));
            accelZList.add(new DataPoint(accelXList.size(), acc_z));
            series_x.appendData(accelXList.get(accelXList.size() - 1), true, MAX_DATA_POINTS);
            series_y.appendData(accelYList.get(accelYList.size() - 1), true, MAX_DATA_POINTS);
            series_z.appendData(accelZList.get(accelZList.size() - 1), true, MAX_DATA_POINTS);
        }

        updateUI(acc_x,acc_y,acc_z);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateUI(float acc_x,float acc_y,float acc_z){
        tv_acc_x.setText("Acceleration x : "+String.format("%2.3f",acc_x)) ;
        tv_acc_y.setText("Acceleration y : "+String.format("%2.3f",acc_y)) ;
        tv_acc_z.setText("Acceleration z : "+String.format("%2.3f",acc_z)) ;

        tv_acc_x_max.setText("Acc max x : "+String.format("%2.3f",max_acc_x)) ;
        tv_acc_y_max.setText("Acc max y : "+String.format("%2.3f",max_acc_y)) ;
        tv_acc_z_max.setText("Acc max z : "+String.format("%2.3f",max_acc_z)) ;

        //Update Graph
        try {

            // Set axis sizing
            if (accelXList.size()>MAX_DATA_POINTS) graph.getViewport().setMinX(accelXList.size()-MAX_DATA_POINTS);
            else graph.getViewport().setMinX(0);

            graph.getViewport().setMaxX(accelXList.size());
            graph.getViewport().setMaxY(Math.max(Math.max(max_acc_x,max_acc_y),max_acc_z));
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setXAxisBoundsManual(true);

        } catch (IllegalArgumentException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
