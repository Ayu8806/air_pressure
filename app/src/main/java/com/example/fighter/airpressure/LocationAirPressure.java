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
public class LocationAirPressure extends SQLiteOpenHelper {


    /**
     * 数据类
     */
        public class Data
        {
            public Long id;
            public String label;
            public float airpressure;
            public  float altitude;
            public float lantitude;
            public  float longtitude;
            public String recordtime;
        }



        private static final String dataBaseName="LocationAirPressure";
        private static final int version =1;


        private static final String createDB="create table airpressureRecord(" +
                "id INTEGER primary key autoincrement," +
                "label char(128) not null ,"+
                "airpressure float not null," +    // comment '气压单位帕斯卡
                "altitude float not null ," +//comment '海拔单位m'
                "lantitude float not null ," +  //comment '纬度'
                "longtitude float not null," +  // comment '精度'
                "recordtime datetime not null " + //comment '记录时间'
                ")";


    public LocationAirPressure(Context context) {
        super(context, dataBaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(createDB);

    }

    public Long InsertData(String label,float airpressure,float altitude,float lantitude,float longtitude)
    {
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
            data.airpressure=cursor.getFloat(i++);
            data.altitude=cursor.getFloat(i++);
            data.lantitude=cursor.getFloat(i++);
            data.longtitude=cursor.getFloat(i++);
            data.recordtime=cursor.getString(i++);
        }

        return list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
