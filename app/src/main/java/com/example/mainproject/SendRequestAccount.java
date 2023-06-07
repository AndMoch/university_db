package com.example.mainproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendRequestAccount extends AppCompatActivity {
    Button cancelReq, sendReq;
    EditText email, name, surname, secondName, univerTitle, univerCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request_account);
        cancelReq = findViewById(R.id.cancelAccountRequest);
        sendReq = findViewById(R.id.sendAccountReq);
        email = findViewById(R.id.emailAccountEmail);
        name = findViewById(R.id.emailAccountName);
        surname = findViewById(R.id.emailAccountSurname);
        secondName = findViewById(R.id.emailAccountSecondName);
        univerCity = findViewById(R.id.emailAccountUniversityCity);
        univerTitle = findViewById(R.id.emailAccountUniversityTitle);
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
                            String body = "Почта - " + email.getText().toString() + "\nИмя - " + name.getText().toString() + "\nФамилия - " + surname.getText().toString() + "\nОтчество - " + secondName.getText().toString() + "\nНазвание университета - " + univerTitle.getText().toString() + "; Город - " + univerCity.getText().toString();
                            sender.sendMail("Заявка на добавление университета", body,
                                    "universitydbbyandmoch@gmail.com", "andmochgeek@ya.ru");
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                    }
                }).start();
                finish();
            }
        });
    }
}