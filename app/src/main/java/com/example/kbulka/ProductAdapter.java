package com.example.kbulka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PRODUCT = 0;
    private static final int VIEW_TYPE_BUTTON = 1;

    private List<Product> productList;
    private DatabaseReference productsRef;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bulka-3f4dd-default-rtdb.europe-west1.firebasedatabase.app");
        productsRef = database.getReference("products");

    }
    public void updateProductList(List<Product> newProductList) {
        productList = newProductList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BUTTON) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_button, parent, false);
            return new ButtonViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            Product product = productList.get(position);

            productHolder.productNameTextView.setText(product.getName());
            productHolder.productImageView.setImageResource(product.getImageResId());

            // Отображение текущего количества из базы данных
            productHolder.tvCurrentQuantity.setText("Количество: " + product.getCurrentQuantity());

            // Установка начального значения переключателя в 0
            productHolder.tvQuantity.setText(String.valueOf(product.getQuantity()));
            product.setQuantityString(String.valueOf(product.getQuantity()));

            productHolder.btnIncrease.setOnClickListener(v -> {
                int newQuantity = Integer.parseInt(productHolder.tvQuantity.getText().toString()) + 1;
                productHolder.tvQuantity.setText(String.valueOf(newQuantity));
                product.setQuantityString(String.valueOf(newQuantity)); // Обновляем quantityString
            });

            productHolder.btnDecrease.setOnClickListener(v -> {
                int newQuantity = Math.max(0, Integer.parseInt(productHolder.tvQuantity.getText().toString()) - 1);
                productHolder.tvQuantity.setText(String.valueOf(newQuantity));
                product.setQuantityString(String.valueOf(newQuantity)); // Обновляем quantityString
            });

        } else if (holder instanceof ButtonViewHolder) {
            ButtonViewHolder buttonHolder = (ButtonViewHolder) holder;
            buttonHolder.btnSave.setOnClickListener(v -> {
                for (Product product : productList) {
                    int addedQuantity = Integer.parseInt(product.getQuantityString());
                    int newTotalQuantity = product.getCurrentQuantity() + addedQuantity;
                    product.setCurrentQuantity(newTotalQuantity);
                    productsRef.child(product.getName()).child("quantity").setValue(newTotalQuantity);
                }
                notifyDataSetChanged();
                Toast.makeText(buttonHolder.btnSave.getContext(), "Данные сохранены", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size() + 1; // +1 для кнопки
    }

    @Override
    public int getItemViewType(int position) {
        return position == productList.size() ? VIEW_TYPE_BUTTON : VIEW_TYPE_PRODUCT;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        ImageButton btnDecrease;
        TextView tvQuantity;
        ImageButton btnIncrease;
        TextView tvCurrentQuantity;
        ProductViewHolder(View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
        }
    }

    static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button btnSave;

        ButtonViewHolder(View itemView) {
            super(itemView);
            btnSave = itemView.findViewById(R.id.btn_save);
        }
    }
}