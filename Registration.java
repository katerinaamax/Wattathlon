package com.wattathlon.wattathlon2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

public class Registration extends AppCompatActivity {

    EditText emailReg, passwordReg, passwordRegC, name, height,
            weight, rowFtp, bikeFtp, skiFtp;
    Button register;
    DataBase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        database = new DataBase(this);

        emailReg = (EditText) findViewById(R.id.emailReg);
        passwordReg = (EditText) findViewById(R.id.passwordReg);
        passwordRegC = (EditText) findViewById(R.id.passwordRegC);
        name = (EditText) findViewById(R.id.nameTab2);
        height = (EditText) findViewById(R.id.heightTab2);
        weight = (EditText) findViewById(R.id.weightTab2);
        rowFtp = (EditText) findViewById(R.id.rowFtpTab2);
        bikeFtp = (EditText) findViewById(R.id.bikeFtpTab2);
        skiFtp = (EditText) findViewById(R.id.skiFtpTab2);
        register = (Button) findViewById(R.id.save);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = emailReg.getText().toString();
                String passStr = passwordReg.getText().toString();
                String passCStr = passwordRegC.getText().toString();
                String nameStr = name.getText().toString();
                String heightStr = height.getText().toString();
                String weightStr = weight.getText().toString();
                String rowFtpStr = rowFtp.getText().toString();
                String bikeFtpStr = bikeFtp.getText().toString();
                String skiFtpStr = skiFtp.getText().toString();

                //if any of the fields is empty
                if(emailStr.equals("") ||
                        passStr.equals("") ||
                        passCStr.equals("") ||
                        nameStr.equals("") ||
                        heightStr.equals("") ||
                        weightStr.equals("") ||
                        rowFtpStr.equals("") ||
                        bikeFtpStr.equals("") ||
                        skiFtpStr.equals(""))

                    Toast.makeText(getApplicationContext(),"There are empty fields.",Toast.LENGTH_SHORT).show();

                else if(!passCStr.equals(passStr))
                    Toast.makeText(getApplicationContext(),"Passwords don't match.",Toast.LENGTH_SHORT).show();

                else if(database.checkEmail(emailStr)) {

                    int height = parseInt(heightStr);
                    int weight = parseInt(weightStr);
                    int rowFtp = parseInt(rowFtpStr);
                    int bikeFtp = parseInt(bikeFtpStr);
                    int skiFtp = parseInt(skiFtpStr);

                    if(database.insert(emailStr, passStr,nameStr, height, weight, rowFtp, bikeFtp, skiFtp)) {
                        Toast.makeText(getApplicationContext(), "Registered successfully!", Toast.LENGTH_SHORT).show();

                        ((Account) getApplication()).setEmail(emailStr);
                        ((Account) getApplication()).setPassword(passStr);
                        ((Account) getApplication()).setAll(emailStr, passStr, getApplicationContext());

                        Intent chooseErgAct = new Intent(getApplicationContext(), Choose.class);
                        startActivity(chooseErgAct);   //if registered, go to next activity
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Email already exists!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
