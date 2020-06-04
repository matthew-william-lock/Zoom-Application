package com.example.accelerometersimulation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // UI Components
    TextView tv_acc_x;
    TextView tv_acc_y;
    TextView tv_acc_z;
    TextView tv_acc_x_max;
    TextView tv_acc_y_max;
    TextView tv_acc_z_max;
    Button bt_get_acc;

    private float max_acc_x=0;
    private float max_acc_y=0;
    private float max_acc_z=0;

    private SensorManager sensorManager;
    private Sensor sensor;

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
        bt_get_acc = findViewById(R.id.bt_get_acc);

        // Accelerometer
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(MainActivity.this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

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

        if (acc_x>max_acc_x) max_acc_x = acc_x;
        if (acc_y>max_acc_y) max_acc_y = acc_y;
        if (acc_z>max_acc_z) max_acc_z = acc_z;

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
    }
}
