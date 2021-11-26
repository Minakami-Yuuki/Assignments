package com.example.myapplicationtest;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class BlankFragment1 extends Fragment {

    private View root;
    private TextView textview;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root == null) {
            // 调用fragment.xml
            root = inflater.inflate(R.layout.fragment_blank, container, false);
        }

        textview = root.findViewById(R.id.text);
        button = root.findViewById(R.id.btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textview.setText("Yes, I am!");
            }
        });

        return root;
    }
}