package com.example.fighter.airpressure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link mainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link mainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainFragment extends Fragment  implements SensorEventListener, Button.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "param2";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.add_cur_location).setOnClickListener(this);
    }

    // TODO: Rename and change types of parameters
    private String title;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //气压
    private float airPressure=0.0f; //单位毫巴
    //海拔
    private float altitude=0.0f; //单位米

    private SensorManager mSensorManager;
    private Sensor pressureSensor;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static mainFragment newInstance(String title, String param2) {
        mainFragment fragment = new mainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public mainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        InitSenSors();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * 更新界面中的传感器数值
     * @param pressure mbar
     */
    private void UpdateDisplay(float pressure)
    {

        LineValue press=new LineValue(getView().findViewById(R.id.press_line));
        LineValue altitude=new LineValue(getView().findViewById(R.id.altitude_line));

        airPressure=pressure*100.0f;
        this.altitude=(float)pressureToattitude(pressure * 100.0f);

        press.SetValue("气压", pressure, "毫巴");
        altitude.SetValue("海拔", this.altitude, "米");
    }



    public void onBtnAddClick(View v) {

        if(v.getId()==R.id.add_cur_location)
        {
            //添加当前地点

            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            final View view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add,null);
            builder.setView(view);
            builder.setTitle("添加");

            builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                    //获得当前的位置
                    LocationManager locationManager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

                    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    {
                        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location!=null)
                        {
                            long id=((MainActivity)getActivity()).getDbHelper().InsertData(((EditText) view.findViewById(R.id.cur_label)).getText().toString(), airPressure, altitude, location.getLatitude(), location.getLongitude());
                            Toast.makeText(getActivity(), "insert id " + id, Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        final Long id=((MainActivity)getActivity()).getDbHelper().InsertData(((EditText)view.findViewById(R.id.cur_label)).getText().toString(),airPressure,altitude,0.0f, 0.0f);
                        Toast.makeText(getActivity(),"insert id "+id,Toast.LENGTH_LONG).show();
                        LocationListener listener=new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                ((MainActivity)getActivity()).getDbHelper().UpdateLocation(id, location.getLatitude(), location.getLongitude());
                                Toast.makeText(getActivity(),"update id "+id,Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        };

                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener,null);
                    }
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


    /**
     * 初始化传感器
     * @return
     */
    private boolean InitSenSors()
    {
        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        if(mSensorManager==null)
        {
            return false;
        }

        pressureSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(pressureSensor==null)
        {
            Toast.makeText(getActivity(),"没有气压传感器",Toast.LENGTH_LONG).show();
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


    /**
     * 实现button点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.add_cur_location)
        {
            onBtnAddClick(v);
        }
    }
}
