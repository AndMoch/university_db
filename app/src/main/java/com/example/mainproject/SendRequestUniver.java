package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendRequestUniver extends AppCompatActivity {
    Button cancelReq, sendReq;
    EditText city, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request_univer);
        cancelReq = findViewById(R.id.cancelUniverRequest);
        sendReq = findViewById(R.id.sendUniverReq);
        city = findViewById(R.id.emailUniverCity);
        name = findViewById(R.id.emailUniverName);
        cancelReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MailSender sender = new MailSender("universitydbbyandmoch@gmail.com",
                                    "aqjmxlwmefzfbvsr");
                            String body = "Название - " + name.getText().toString() + "; Город - " + city.getText().toString();
                            sender.sendMail("Заявка на добавление университета", body,
                                    "universitydbbyandmoch@gmail.com", "andmochgeek@ya.ru");
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                    }
                }).start();
                finish();

//                aqjmxlwmefzfbvsr
            }
        });
    }
}