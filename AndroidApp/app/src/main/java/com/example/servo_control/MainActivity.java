package com.example.servo_control;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.RecognizerIntent;
import android.util.PrintWriterPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int PORT = 40300;
    public static final String addr = "192.168.0.17";

    private static final int VOICE_RECORD_CODE = 1;

    private Button btn1, btn2, btn3, btn4, speachBtn;
    private double value1, value2, value3, value4, value5;
    private SeekBar seekBar;

    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        speachBtn = findViewById(R.id.speachBtn);
        seekBar = findViewById(R.id.bar);

        value1 = 33;
        value2 = 1;
        value3 = 5;
        value4 = 15;


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move_servo(value);
//                Control_Thread thread = new Control_Thread(value1);
//                thread.start();
                Sender sender = new Sender();
                sender.execute((int)(value1));
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move_servo(value);
//                Control_Thread thread = new Control_Thread(value2);
//                thread.start();

                Sender sender = new Sender();
                sender.execute((int)(value2));
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sender sender = new Sender();
                sender.execute((int)(value3));
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sender sender = new Sender();
                sender.execute((int) value4);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Sender sender = new Sender();
                sender.execute(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speachIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speachIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speachIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speach to text");
                startActivityForResult(speachIntent, VOICE_RECORD_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == VOICE_RECORD_CODE && resultCode == RESULT_OK){
            ArrayList<String> records = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String record = records.get(0);

            if(record.equals("up")){
                //value = 200;
                Sender sender = new Sender();
                sender.execute(26);
            }
            else if(record.equals("down")){
                //value = 100;
                Sender sender = new Sender();
                sender.execute(0);
            }
            else{
                Toast.makeText(this, "Say UP or DOWN to move motor", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}