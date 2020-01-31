package com.shehanintl.shehan.roboarm_v11;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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

public class CoordinatedMode extends AppCompatActivity {

    Button testbtn;
    Button mode;
    EditText j1Text,j2Text,j3Text,j4Text,j5Text,j6Text;
    ImageButton j1Up,j1Down,j2Up,j2Down,j3Up,j3Down,j4Up,j4Down,j5Up,j5Down,j6Up,j6Down;
    ImageButton j1Plus,j1Minus,j2Plus,j2Minus,j3Plus,j3Minus,j4Plus,j4Minus,j5Plus,j5Minus,j6Plus,j6Minus;


    TextView lbl1,lbl2,lbl3,lbl5;
    TextView lbl6;

    String j1,j2,j3,j4,j5;

    String operating_mode;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    String readMessage;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinated_mode);

        operating_mode="BC";

        testbtn=(Button) findViewById(R.id.button);

        j1Plus=findViewById(R.id.j1Plus);
        j1Minus=findViewById(R.id.j1Minus);
        j3Plus=findViewById(R.id.j3Plus);
        j3Minus=findViewById(R.id.j3Minus);
        j5Plus=findViewById(R.id.j5Plus);
        j5Minus=findViewById(R.id.j5Minus);
        j2Plus=findViewById(R.id.j2Plus);
        j2Minus=findViewById(R.id.j2Minus);
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

        mode=findViewById(R.id.mode);

        lbl1=findViewById(R.id.lbl_j1);
        lbl2=findViewById(R.id.lbl_j2);
        lbl3=findViewById(R.id.lbl_j3);
        lbl5=findViewById(R.id.lbl_j5);
        lbl6=findViewById(R.id.lbl_j6);

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        j1="A";j2="S";j3="N";j4="D";j5="E";

        mode.setText("SWITCH TO EF");

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

    public void updateSteps(View view) {

//        String steps=String.format("#"+"J1"+j1_int.getValue()+"J2"+j2_int.getValue()+"J3"+j3_int.getValue()+"J5"+j5_int.getValue()+"J6"+j6_int.getValue()+"~");
//        mConnectedThread.write(steps);
    }

    public void j1Plus(View view) {
        byte a=1;
        byte b=(byte) Integer.parseInt(j1Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j1);
        mConnectedThread.writeByte(a);
        mConnectedThread.writeByte(b);
    }

    public void j6Plus(View view) {
        byte a=6;
        byte b=1;
        byte c=(byte) Integer.parseInt(j5Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j5);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j6Minus(View view) {
        byte a=6;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j5Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j5);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j1Minus(View view) {
        byte a=1;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j1Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j1);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j3Plus(View view) {
        byte a=3;
        byte b=1;
        byte c=(byte) Integer.parseInt(j3Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j3);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j3Minus(View view) {
        byte a=3;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j3Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j3);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j5Minus(View view) {
        byte a=5;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j4Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j4);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j5Plus(View view) {
        byte a=5;
        byte b=1;
        byte c=(byte) Integer.parseInt(j4Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j4);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j2Plus(View view) {
        byte a=2;
        byte b=1;
        byte c=(byte) Integer.parseInt(j2Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j2);
        mConnectedThread.writeByte(b);
        mConnectedThread.writeByte(c);
    }

    public void j2Minus(View view) {
        byte a=2;
        byte b=-1;
        byte c=(byte) Integer.parseInt(j2Text.getText().toString());
        mConnectedThread.write(operating_mode);
        mConnectedThread.write(j2);
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

    public void changeMode(View view) {
        if (mode.getText().equals("SWITCH TO EF")){
            mode.setText("SWITCH TO BASE");
            j6Plus.setVisibility(View.INVISIBLE);
            j6Minus.setVisibility(View.INVISIBLE);
            j5Plus.setVisibility(View.INVISIBLE);
            j5Minus.setVisibility(View.INVISIBLE);
            lbl5.setVisibility(View.INVISIBLE);
            lbl6.setVisibility(View.INVISIBLE);
            j4Text.setVisibility(View.INVISIBLE);
            j4Up.setVisibility(View.INVISIBLE);
            j4Down.setVisibility(View.INVISIBLE);
            j5Text.setVisibility(View.INVISIBLE);
            j5Up.setVisibility(View.INVISIBLE);
            j5Down.setVisibility(View.INVISIBLE);
            lbl1.setText("X");
            lbl2.setText("Y");
            lbl3.setText("Z");
            operating_mode="BE";
            j1="X";j2="Y";j3="Z";
        }else if (mode.getText().equals("SWITCH TO BASE")){
            mode.setText("SWITCH TO EF");
            j6Plus.setVisibility(View.VISIBLE);
            j6Minus.setVisibility(View.VISIBLE);
            j5Plus.setVisibility(View.VISIBLE);
            j5Minus.setVisibility(View.VISIBLE);
            lbl5.setVisibility(View.VISIBLE);
            lbl6.setVisibility(View.VISIBLE);
            j4Text.setVisibility(View.VISIBLE);
            j4Up.setVisibility(View.VISIBLE);
            j4Down.setVisibility(View.VISIBLE);
            j5Text.setVisibility(View.VISIBLE);
            j5Up.setVisibility(View.VISIBLE);
            j5Down.setVisibility(View.VISIBLE);
            lbl1.setText("Approach");
            lbl2.setText("Slider");
            lbl3.setText("Normal");
            lbl5.setText("Approach Direction");
            lbl6.setText("Slide Direction");
            operating_mode="BC";
            j1="A";j2="S";j3="N";j4="D";j5="E";
        }else{
            System.out.println("error");
            mode.setText("BASE");
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
