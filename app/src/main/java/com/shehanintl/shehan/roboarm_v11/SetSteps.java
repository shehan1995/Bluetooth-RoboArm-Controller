package com.shehanintl.shehan.roboarm_v11;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SetSteps extends AppCompatActivity {

    Button testbtn;
    ScrollableNumberPicker j1_int;
    ScrollableNumberPicker j2_int;
    ScrollableNumberPicker j3_int;
    ScrollableNumberPicker j4_int;
    ScrollableNumberPicker j5_int;
    ScrollableNumberPicker j6_int;
    ScrollableNumberPicker j1_float;
    ScrollableNumberPicker j2_float;
    ScrollableNumberPicker j3_float;
    ScrollableNumberPicker j4_float;
    ScrollableNumberPicker j5_float;
    ScrollableNumberPicker j6_float;
    Button j1Plus,j1Minus,j3Plus,j3Minus,j5Plus,j5Minus;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    String readMessage;
    String realMessage;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_steps);

        testbtn=(Button) findViewById(R.id.button);
        j1_int=findViewById(R.id.snp_j1_int);
        j2_int=findViewById(R.id.snp_j2_int);
        j3_int=findViewById(R.id.snp_j3_int);
        j4_int=findViewById(R.id.snp_j4_int);
        j5_int=findViewById(R.id.snp_j5_int);
        j6_int=findViewById(R.id.snp_j6_int);
        j1_float=findViewById(R.id.snp_j1_float);
        j2_float=findViewById(R.id.snp_j2_float);
        j3_float=findViewById(R.id.snp_j3_float);
        j4_float=findViewById(R.id.snp_j4_float);
        j5_float=findViewById(R.id.snp_j5_float);
        j6_float=findViewById(R.id.snp_j6_float);

        j1Plus=findViewById(R.id.j1Plus);
        j1Minus=findViewById(R.id.j1Minus);
        j3Plus=findViewById(R.id.j3Plus);
        j3Minus=findViewById(R.id.j3Minus);
        j5Plus=findViewById(R.id.j5Plus);
        j5Minus=findViewById(R.id.j5Minus);

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String steps=String.format("#"+j1_int.getValue()+"."+j1_float.getValue()+","+j2_int.getValue()+"."+j2_float.getValue()+","+j3_int.getValue()+"."+j3_float.getValue()+","+j4_int.getValue()+"."+j4_float.getValue()+","+j5_int.getValue()+"."+j5_float.getValue()+","+j6_int.getValue()+"."+j6_float.getValue()+"~");
                mConnectedThread.write(steps);
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    readMessage = new String(buffer, 0, bytes);
//                    System.out.println(readMessage);
                    // Send the obtained bytes to the UI Activity via handler
//                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
