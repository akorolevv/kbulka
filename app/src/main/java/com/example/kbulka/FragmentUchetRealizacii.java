package com.example.kbulka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentUchetRealizacii extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uchet_realizacii, container, false);
        TextView textView = view.findViewById(R.id.text_uchet_realizacii);
        textView.setText("Фрагмент для учета реализации");
        return view;
    }
}