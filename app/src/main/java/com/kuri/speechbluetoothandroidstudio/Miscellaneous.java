package com.kuri.speechbluetoothandroidstudio;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Kuri on 2018-01-03.
 */

public class Miscellaneous {

    public void displayErrorToast(String error, Context context){
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    public void displaySuccessToast(String success, Context context){
        Toast.makeText(context, success, Toast.LENGTH_LONG).show();
    }
}