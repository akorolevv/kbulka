package com.example.kbulka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;



public class FragmentUchetVypuska extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference productsRef;
    private UchetVypuskaViewModel viewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uchet_vypuska, container, false);

        Button btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            // Сохранение данных в Firebase
            for (Product product : productList) {
                productsRef.child(product.getName()).setValue(product); // Используем имя продукта как ключ
            }
            Toast.makeText(getContext(), "Данные сохранены", Toast.LENGTH_SHORT).show();
        });
        recyclerView = view.findViewById(R.id.rv_uchet_vypuska);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();

        viewModel = new ViewModelProvider(this).get(UchetVypuskaViewModel.class);
        viewModel.getProductListLiveData().observe(getViewLifecycleOwner(), products -> {
            adapter.updateProductList(products);
        });

        // Заполнение списка предопределенными продуктами
        productList.add(new Product("Белый хлеб", R.drawable.ic_production, 0));
        productList.add(new Product("Ржаной хлеб", R.drawable.ic_production, 0));
        // ... (добавьте другие продукты)

        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        // Загрузка данных из Firebase (если нужно обновить количества из БД)
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bulka-3f4dd-default-rtdb.europe-west1.firebasedatabase.app");
        productsRef = database.getReference("products");
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                viewModel.setProductList(productList);
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String productName = productSnapshot.child("name").getValue(String.class);
                    Integer productQuantity = productSnapshot.child("quantity").getValue(Integer.class);

                    // Находим продукт в списке по имени и обновляем его количество
                    for (Product product : productList) {
                        if (product.getName().equals(productName)) {
                            product.setQuantity(productQuantity != null ? productQuantity : 0);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок
            }
        });


        return view;
    }
}