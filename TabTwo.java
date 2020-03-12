package com.wattathlon.wattathlon2;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TabTwo extends Fragment {

    EditText name, height, weight, rowFtp, bikeFtp, skiFtp;
    TextView nameTxt, heightTxt,weightTxt, rowTxt, bikeTxt, skiTxt;
    Button save;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.tab_two, container, false);

        nameTxt = (TextView) view.findViewById(R.id.nameTxt);
        heightTxt = (TextView) view.findViewById(R.id.heightTxt);
        weightTxt = (TextView) view.findViewById(R.id.weightTxt);
        nameTxt = (TextView) view.findViewById(R.id.nameTxt);
        rowTxt = (TextView) view.findViewById(R.id.rowTxt);
        bikeTxt = (TextView) view.findViewById(R.id.bikeTxt);
        skiTxt = (TextView) view.findViewById(R.id.skiTxt);

        name = (EditText) view.findViewById(R.id.nameTab2);
        height = (EditText) view.findViewById(R.id.heightTab2);
        weight = (EditText) view.findViewById(R.id.weightTab2);
        rowFtp = (EditText) view.findViewById(R.id.rowFtpTab2);
        bikeFtp = (EditText) view.findViewById(R.id.bikeFtpTab2);
        skiFtp = (EditText) view.findViewById(R.id.skiFtpTab2);

        name.setText(((Account) getActivity().getApplication()).getName());
        height.setText(String.valueOf(((Account) getActivity().getApplication()).getHeight()));
        weight.setText(String.valueOf(((Account) getActivity().getApplication()).getWeight()));
        rowFtp.setText(String.valueOf(((Account) getActivity().getApplication()).getRowFtp()));
        bikeFtp.setText(String.valueOf(((Account) getActivity().getApplication()).getBikeFtp()));
        skiFtp.setText(String.valueOf(((Account) getActivity().getApplication()).getSkiFtp()));

        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals("") ||
                        height.getText().toString().equals("") ||
                        weight.getText().toString().equals("") ||
                        rowFtp.getText().toString().equals("") ||
                        bikeFtp.getText().toString().equals("") ||
                        skiFtp.getText().toString().equals("")) {

                    Toast.makeText(getActivity().getApplicationContext(),"There are empty fields.",Toast.LENGTH_SHORT).show();
                }
                else {
                    String newName = name.getText().toString();
                    int newHeight = Integer.parseInt(height.getText().toString());
                    int newWeight = Integer.parseInt(weight.getText().toString());
                    int newRowFtp = Integer.parseInt(rowFtp.getText().toString());
                    int newBikeFtp = Integer.parseInt(bikeFtp.getText().toString());
                    int newSkiFtp = Integer.parseInt(skiFtp.getText().toString());


                    DataBase base = new DataBase(getActivity().getApplicationContext());

                    ContentValues newValues = new ContentValues();

                    newValues.put("name", newName);
                    newValues.put("height", newHeight);
                    newValues.put("weight", newWeight);
                    newValues.put("rowFtp", newRowFtp);
                    newValues.put("bikeFtp", newBikeFtp);
                    newValues.put("skiFtp", newSkiFtp);

                    String email = ((Account) getActivity().getApplication()).getEmail();
                    String password = ((Account) getActivity().getApplication()).getPassword();

                    //If database is updated then update the fields of Account
                    if (base.update(newValues, email)) {
                        ((Account) getActivity().getApplication()).setAll(email, password, getActivity().getApplicationContext());
                        Toast.makeText(getActivity().getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}
