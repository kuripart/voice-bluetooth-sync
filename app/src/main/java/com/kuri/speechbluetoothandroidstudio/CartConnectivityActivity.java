package com.kuri.speechbluetoothandroidstudio;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class CartConnectivityActivity extends AppCompatActivity {


    Button btnPaired;
    ListView deviceList;

    //Bluetooth parameters
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address"; // This will be accessed in another activity, hence Public
    Miscellaneous miscellaneousFunctions = new Miscellaneous();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_connectivity);

        //Calling widgets
        btnPaired = (Button)findViewById(R.id.button_cart);
        deviceList = (ListView)findViewById(R.id.listViewCart);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        //when Bluetooth support not present
        if(myBluetooth == null) {
            //Show a message: device has no bluetooth adapter
            miscellaneousFunctions.displayErrorToast("Bluetooth Device Not Available", getApplicationContext());

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            //An automatic request will be sent, say Yes
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //show the list of paired devices
                pairedDevicesList();
            }
        });
    }


    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0) {
            for(BluetoothDevice bt : pairedDevices) {
                //loop through the paired devices and add to the list view
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            miscellaneousFunctions.displayErrorToast("No Paired Bluetooth Devices Found.", getApplicationContext());
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity.
            Intent i = new Intent(CartConnectivityActivity.this, ManualCartControlsActivity.class);

            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address);
            startActivity(i);
        }
    };
}
