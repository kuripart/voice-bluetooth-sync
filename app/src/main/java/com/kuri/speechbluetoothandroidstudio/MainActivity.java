package com.kuri.speechbluetoothandroidstudio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    ImageButton armButton, cartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        armButton = (ImageButton) findViewById(R.id.control_arm_button);
        cartButton = (ImageButton) findViewById(R.id.control_cart_button);

        armButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToArmConnectivityPage = new Intent(MainActivity.this, ArmConnectivityActivity.class);
                startActivity(goToArmConnectivityPage);
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCartConnectivityPage = new Intent(MainActivity.this, CartConnectivityActivity.class);
                startActivity(goToCartConnectivityPage);

            }
        });


    }


}
