package com.wattathlon.wattathlon2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView welcomeTxt,toTxt, wattathlonTxt, or;
    EditText emailText, passwordText;
    Button login, createAccount;
    DataBase base;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeTxt = (TextView) findViewById(R.id.welcome);
        toTxt = (TextView) findViewById(R.id.to);
        wattathlonTxt = (TextView) findViewById(R.id.wattathlon);
        or = (TextView) findViewById(R.id.or);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        login = (Button) findViewById(R.id.login);
        createAccount = (Button) findViewById(R.id.createAccount);
        base = new DataBase (this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailLogin = emailText.getText().toString();
                String passwordLogin = passwordText.getText().toString();

                if(!base.checkEmail(emailLogin)) {  //account registered in database

                    if (base.emailAndPass(emailLogin, passwordLogin)) { //successful login
                        /*Intent chooseErg = new Intent(getApplicationContext(), ChooseErg.class);

                        //send ftp values to the next activity
                        chooseErg.putExtra("FTP", new int[] {base.getRowFtp(emailLogin,passwordLogin),
                                                                base.getBikeFtp(emailLogin, passwordLogin),
                                                                base.getSkiFtp(emailLogin, passwordLogin), 0});
                        startActivity(chooseErg);*/

                        //Account is global variable
                        ((Account) getApplication()).setEmail(emailLogin);
                        ((Account) getApplication()).setPassword(passwordLogin);
                        ((Account) getApplication()).setAll(emailLogin, passwordLogin, MainActivity.this);

                        Intent tabbed = new Intent(getApplicationContext(), Choose.class);
                        startActivity(tabbed);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Wrong password!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Account not registered",Toast.LENGTH_SHORT).show();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {  //start registration activity
            @Override
            public void onClick(View view) {
                Intent registration = new Intent(getApplicationContext(), Registration.class);
                startActivity(registration);
            }
        });
    }
}

