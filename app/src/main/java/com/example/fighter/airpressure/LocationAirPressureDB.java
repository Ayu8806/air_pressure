package com.example.fighter.airpressure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by practice on 15-9-1.
 */
public class LocationAirPressureDB extends SQLiteOpenHelper {


    /**
     * 数据类
     */
        public class Data
        {
            public Long id;
            public String label;
            public double airpressure;
            public  double altitude;
            public double lantitude;
            public  double longtitude;
            public String recordtime;
        }



        private static final String dataBaseName="LocationAirPressure";
        private static final int version =1;


        private static final String createDB="create table airpressureRecord(" +
                "id INTEGER primary key autoincrement," +
                "label char(128) not null ,"+
                "airpressure double not null," +    // comment '气压单位帕斯卡
                "altitude double not null ," +//comment '海拔单位m'
                "lantitude double not null ," +  //comment '纬度'
                "longtitude double not null," +  // comment '精度'
                "recordtime datetime not null " + //comment '记录时间'
                ")";


    public LocationAirPressureDB(Context context) {
        super(context, dataBaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(createDB);

    }

    public Long InsertData(String label,double airpressure,double altitude,double lantitude,double longtitude)
    {
        if(label.compareTo("")==0)
        {
            label=new SimpleDateFormat("yyyyMMDDHHMM").format(new Data());
        }

        ContentValues value=new ContentValues();
        value.put("label",label);
        value.put("airpressure",airpressure);
        value.put("altitude",altitude);
        value.put("lantitude",lantitude);
        value.put("longtitude",longtitude);

        value.put("recordtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));


        Long id=this.getWritableDatabase().insert("airpressureRecord","",value);
        return id;

    }

    public int UpdateLocation(long id,double lantitude ,double longtitude)
    {
        ContentValues value=new ContentValues();
        value.put("lantitude", lantitude);
        value.put("longtitude", longtitude);
        return getWritableDatabase().update("airpressureRecord",value,"id="+id,null);
    }


    public List<Data> getAll()
    {

        Cursor cursor=this.getReadableDatabase().rawQuery("select id,label,airpressure,altitude,lantitude,longtitude,recordtime from airpressureRecord", null);
        List<Data> list=new ArrayList<>(cursor.getCount());
        while(cursor.moveToNext())
        {
            Data data=new Data();
            int i=0;
            data.id=cursor.getLong(i++);
            data.label=cursor.getString(i++);
            data.airpressure=cursor.getDouble(i++);
            data.altitude=cursor.getDouble(i++);
            data.lantitude=cursor.getDouble(i++);
            data.longtitude=cursor.getDouble(i++);
            data.recordtime=cursor.getString(i++);
            list.add(data);
        }

        return list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
