package com.example.fighter.airpressure;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor pressureSensor;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!InitSenSors())
        {
            finish();
        }



        String [] navigationArr=getResources().getStringArray(R.array.navigatoin_action);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.draw_item, R.id.content, navigationArr));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                 R.string.open_drawer,R.string.close_drawer) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_48dp);



        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setSelection(0);

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

        Log.i("fDebug","action click:"+id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==android.R.id.home)
        {
            Log.i("fDebug","home as up click");
            mDrawerLayout.openDrawer(mDrawerList);
            return true;

        }
        else if(id==R.id.home)
        {
            Log.i("fDebug","home click");
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * 更新界面中的传感器数值
     * @param pressure mbar
     */
    private void UpdateDisplay(float pressure)
    {

        LineValue press=new LineValue(findViewById(R.id.press_line));
        LineValue altitude=new LineValue(findViewById(R.id.altitude_line));

        press.SetValue("气压",pressure,"毫巴");
        altitude.SetValue("海拔", pressureToattitude(pressure * 100.0f), "米");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * 初始化传感器
     * @return
     */
    private boolean InitSenSors()
    {

        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        if(mSensorManager==null)
        {
            return false;
        }

        pressureSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(pressureSensor==null)
        {
            Toast.makeText(this,"没有气压传感器",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        UpdateDisplay(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 将气压转换成高度
     * 参考：https://en.wikipedia.org/wiki/Atmospheric_pressure#Altitude_variation
     * @param pressure
     * @return
     */

    public static double pressureToattitude(float pressure)
    {
        double p0=101325;
        double R=8.31447;
        double T0=288.15;
        double g=9.80665;
        double M=0.0289644;
        float p=pressure;

        return 0.0-java.lang.Math.log(p/p0)*((R*T0)/(g*M));
    }


    public void onBtnAddClick(View v) {

        if(v.getId()==R.id.add_cur_location)
        {

            //添加当前地点

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            View view=LayoutInflater.from(this).inflate(R.layout.dialog_add,null);
            builder.setView(view);
            builder.setTitle("添加");

            builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            
        }

    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }


}



