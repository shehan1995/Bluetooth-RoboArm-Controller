package com.shehanintl.shehan.roboarm_v11;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class IndependentJoint extends AppCompatActivity {

    EditText j1Text,j2Text,j3Text,j4Text,j5Text,j6Text;
    ImageButton j1Up,j1Down,j2Up,j2Down,j3Up,j3Down,j4Up,j4Down,j5Up,j5Down,j6Up,j6Down;
    ImageButton j1Plus,j1Minus,j2Plus,j2Minus,j3Plus,j3Minus,j4Plus,j4Minus,j5Plus,j5Minus,j6Plus,j6Minus;

    Handler bluetoothIn;
    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    String readMessage;
    String realMessage;

    TextView txtString, txtLength;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_independent_joint);

        j1Plus=findViewById(R.id.j1Plus);
        j1Minus=findViewById(R.id.j1Minus);
        j3Plus=findViewById(R.id.j3Plus);
        j3Minus=findViewById(R.id.j3Minus);
        j5Plus=findViewById(R.id.j5Plus);
        j5Minus=findViewById(R.id.j5Minus);
        j2Plus=findViewById(R.id.j2Plus);
        j2Minus=findViewById(R.id.j2Minus);
        j4Plus=findViewById(R.id.j4Plus);
        j4Minus=findViewById(R.id.j4Minus);
        j6Plus=findViewById(R.id.j6Plus);
        j6Minus=findViewById(R.id.j6Minus);

        j1Text=findViewById(R.id.j1Text);
        j2Text=findViewById(R.id.j2Text);
        j3Text=findViewById(R.id.j3Text);
        j4Text=findViewById(R.id.j4Text);
        j5Text=findViewById(R.id.j5Text);
        j6Text=findViewById(R.id.j6Text);

        j1Up=findViewById(R.id.j1Up);
        j2Up=findViewById(R.id.j2Up);
        j3Up=findViewById(R.id.j3Up);
        j4Up=findViewById(R.id.j4Up);
        j5Up=findViewById(R.id.j5Up);
        j6Up=findViewById(R.id.j6Up);
        j6Down=findViewById(R.id.j6Down);
        j5Down=findViewById(R.id.j5Down);
        j4Down=findViewById(R.id.j4Down);
        j3Down=findViewById(R.id.j3Down);
        j2Down=findViewById(R.id.j2Down);
        j1Down=findViewById(R.id.j1Down);

        txtString=findViewById(R.id.textView);
        txtLength=findViewById(R.id.textView2);

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    System.out.println("test"+readMessage);
                    if(readMessage.equals("")){
                        System.out.println("if");
                    }
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    txtString.setText("Data Received = " + readMessage);
                    int dataLength = recDataString.length();                          //get length of data received
                    txtLength.setText("String Length = " + String.valueOf(dataLength));

                    recDataString.delete(0, recDataString.length());                    //clear all string data
                    // strIncom =" ";
                }
            }
        };

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

    public void j1Plus(View view) {
        byte a=1;
        byte b=(byte) Integer.parseInt(j1Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
    }

    public void j6Plus(View view) {
        byte a=6;
        byte b=1;
        byte c=(byte) Integer.parseInt(j6Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j6Minus(View view) {
        byte a=6;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j6Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j1Minus(View view) {
        byte a=1;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j1Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j3Plus(View view) {
        byte a=3;
        byte b=1;
        byte c=(byte) Integer.parseInt(j3Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j3Minus(View view) {
        byte a=3;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j3Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j5Minus(View view) {
        byte a=5;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j5Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j5Plus(View view) {
        byte a=5;
        byte b=1;
        byte c=(byte) Integer.parseInt(j5Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j2Plus(View view) {
        byte a=2;
        byte b=1;
        byte c=(byte) Integer.parseInt(j2Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j2Minus(View view) {
        byte a=2;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j2Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j4Plus(View view) {
        byte a=4;
        byte b=1;
        byte c=(byte) Integer.parseInt(j4Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j4Minus(View view) {
        byte a=2;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j4Text.getText().toString());
        mConnectedThread.write("BS");
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j1Up(View view) {
        try {
            j1Text.setText(""+(Integer.parseInt(j1Text.getText().toString())+1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j2Up(View view) {
        try {
            j2Text.setText(""+(Integer.parseInt(j2Text.getText().toString())+1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j3Up(View view) {
        try {
            j3Text.setText(""+(Integer.parseInt(j3Text.getText().toString())+1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j4Up(View view) {
        try {
            j4Text.setText(""+(Integer.parseInt(j4Text.getText().toString())+1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j5Up(View view) {
        try {
            j5Text.setText(""+(Integer.parseInt(j5Text.getText().toString())+1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j6Up(View view) {
        try {
            j6Text.setText(""+(Integer.parseInt(j6Text.getText().toString())+1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j1Down(View view) {
        try {
            j1Text.setText(""+(Integer.parseInt(j1Text.getText().toString())-1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j2Down(View view) {
        try {
            j2Text.setText(""+(Integer.parseInt(j2Text.getText().toString())-1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j3Down(View view) {
        try {
            j3Text.setText(""+(Integer.parseInt(j3Text.getText().toString())-1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j4Down(View view) {
        try {
            j4Text.setText(""+(Integer.parseInt(j4Text.getText().toString())-1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j5Down(View view) {
        try {
            j5Text.setText(""+(Integer.parseInt(j5Text.getText().toString())-1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
        }
    }
    public void j6Down(View view) {
        try {
            j6Text.setText(""+(Integer.parseInt(j6Text.getText().toString())-1));
        }catch (Exception NumberFormatException){
            Toast.makeText(getBaseContext(), "Enter a value", Toast.LENGTH_LONG).show();
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
//                    System.out.println("read"+readMessage);
//                    System.out.println("byte"+bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
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

        public void writeByte(byte input) {
            try {
                mmOutStream.write(input);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
