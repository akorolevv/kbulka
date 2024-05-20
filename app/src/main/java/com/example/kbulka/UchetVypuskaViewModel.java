package com.example.kbulka;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class UchetVypuskaViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> productListLiveData = new MutableLiveData<>();

    public LiveData<List<Product>> getProductListLiveData() {
        return productListLiveData;
    }

    public void setProductList(List<Product> productList) {
        productListLiveData.setValue(productList);
    }
}