package com.example.kbulka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

public class FragmentAnalizDannyh extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analiz_dannyh, container, false);
        TextView textView = view.findViewById(R.id.text_analiz_dannyh);
        textView.setText("Фрагмент для анализа данных");
        return view;
    }
}
