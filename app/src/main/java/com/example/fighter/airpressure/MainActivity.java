package com.example.fighter.airpressure;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements  OnFragmentInteractionListener{

    public final static int main_fragment=0;
    public final static int record_fragment=1;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    String [] navigationArr=null;
    public final static String []fragement_tagArr={"main_fragement_tag","record_fragement_tag"};

    private Fragment curFragment;


    public LocationAirPressureDB getDbHelper() {
        return dbHelper;
    }

    private LocationAirPressureDB dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        navigationArr=getResources().getStringArray(R.array.navigatoin_action);
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

        mDrawerList.setItemChecked(0, true);

        dbHelper=new LocationAirPressureDB(this);
        getSupportActionBar().setTitle(navigationArr[0]);


        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        Fragment fragment = mainFragment.newInstance(navigationArr[0], fragement_tagArr[0]);
        fragmentTransaction.add(R.id.fragment_container, fragment, fragement_tagArr[0]);
        fragmentTransaction.show(fragment);
        if(fragment.isDetached())
            fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
        curFragment=fragment;
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


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

        getSupportActionBar().setTitle(navigationArr[position]);

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        Fragment fragment=null;

        switch (position)
        {

            case main_fragment:
            {

                fragment=fragmentManager.findFragmentByTag(fragement_tagArr[position]);
                if(fragment==null)
                {
                    fragment=mainFragment.newInstance(navigationArr[position],fragement_tagArr[position]);
                    fragmentTransaction.add(R.id.fragment_container, fragment, fragement_tagArr[position]);
                }

            }
            break;


            case record_fragment:
            {
                fragment=fragmentManager.findFragmentByTag(fragement_tagArr[position]);
                if(fragment==null)
                {
                    fragment=RecordFragment.newInstance(navigationArr[position],fragement_tagArr[position]);
                    fragmentTransaction.add(R.id.fragment_container, fragment, fragement_tagArr[position]);
                }
            }
            break;
        }

        Log.i("fDebug","curFragment :"+(curFragment==null ?"null ":"not null"));
        if(curFragment!=null)
            fragmentTransaction.hide(curFragment);
        if(fragment.isDetached())
            fragmentTransaction.attach(fragment);
        fragmentTransaction.show(fragment);
        curFragment=fragment;
        fragmentTransaction.commit();

        mDrawerLayout.closeDrawer(mDrawerList);
    }


}



