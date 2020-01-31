package com.shehanintl.shehan.roboarm_v11;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.shehanintl.shehan.roboarm_v11.MainActivity.EXTRA_DEVICE_ADDRESS;

public class SelectMode extends AppCompatActivity {

    Button independent_mode;
    Button coordinate_mode;

    // String for MAC address
    private static String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        independent_mode=findViewById(R.id.independent);
        coordinate_mode=findViewById(R.id.coordinated);

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);
    }

    public void selectIndependent(View view) {
//        startActivity(new Intent(SelectMode.this,SetSteps.class));
        Intent i = new Intent(SelectMode.this, IndependentJoint.class);
        i.putExtra(EXTRA_DEVICE_ADDRESS, address);
        startActivity(i);
    }

    public void selectCordinated(View view) {
        Intent i = new Intent(SelectMode.this, CoordinatedMode.class);
        i.putExtra(EXTRA_DEVICE_ADDRESS, address);
        startActivity(i);
    }
}
