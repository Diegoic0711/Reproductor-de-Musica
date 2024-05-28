package com.example.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class fragment1 extends Fragment {

    private EditText ed1, ed2;
    private TextView txt1;
    private Button btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ed1 = view.findViewById(R.id.ed1);
        ed2 = view.findViewById(R.id.ed2);
        txt1 = view.findViewById(R.id.txt1);
        btn = view.findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               int val1 = Integer.parseInt(ed1.getText().toString());
               int val2 = Integer.parseInt(ed2.getText().toString());
               int suma = val1+val2;
               txt1.setText("La Suma es:" + suma);
           }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
