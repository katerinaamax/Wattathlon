package com.wattathlon.wattathlon2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TabOne extends Fragment {
    public  static final String TAG = "ChooseErg";
    int  []ftp = new int[4];

    DataBase base;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_one, container, false);

        TextView chooseErg = (TextView) view.findViewById(R.id.chooseErg);

        Button rowButton = (Button) view.findViewById(R.id.rowButton);
        rowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Account) getActivity().getApplication()).setErgType("row");

                Intent bleConnection = new Intent(getActivity().getApplicationContext(), BLEconnection.class);
                startActivity(bleConnection);
            }
        });

        Button bikeButton = (Button) view.findViewById(R.id.bikeButton);
        bikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Account) getActivity().getApplication()).setErgType("bike");

                Intent bleConnection = new Intent(getActivity().getApplicationContext(), BLEconnection.class);
                startActivity(bleConnection);
            }
        });

        Button skiButton = (Button) view.findViewById(R.id.skiButton);
        skiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Account) getActivity().getApplication()).setErgType("ski");

                Intent bleConnection = new Intent(getActivity().getApplicationContext(), BLEconnection.class);
                startActivity(bleConnection);
            }
        });

        return view;
    }
}
