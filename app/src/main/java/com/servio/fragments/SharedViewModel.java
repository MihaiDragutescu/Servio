package com.servio.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.servio.models.Dish;

import java.util.List;

public class SharedViewModel extends ViewModel {
    public MutableLiveData<CharSequence> text = new MutableLiveData<>();

    MutableLiveData<List<Dish>> dishList = new MutableLiveData<>();

   // MutableLiveData<Boolean> closeFragment = new MutableLiveData<>();

    public void setText(CharSequence input) {
        text.setValue(input);
    }

    public void setDishList(List<Dish> dishList1) {
        dishList.setValue(dishList1);
    }

    public LiveData<CharSequence> getText() {
        return text;
    }
}
