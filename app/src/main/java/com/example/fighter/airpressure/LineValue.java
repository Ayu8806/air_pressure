package com.example.fighter.airpressure;

import android.view.View;
import android.widget.TextView;

/**
 * Created by fighter on 15-8-30.
 */
public class LineValue {

//    private String attr_name;
//    private double attr_value;
//    private String attr_unit;

    private View view;

    public LineValue(View view)
    {
        this.view=view;
    }

    public void SetValue(String attr_name,double attr_value,String attr_unit)
    {
        ((TextView)view.findViewById(R.id.attribute_name)).setText(attr_name);
        ((TextView)view.findViewById(R.id.attribute_value)).setText(new java.text.DecimalFormat("#.00").format(attr_value));
        ((TextView)view.findViewById(R.id.attribute_unit)).setText(attr_unit);
    }


}
