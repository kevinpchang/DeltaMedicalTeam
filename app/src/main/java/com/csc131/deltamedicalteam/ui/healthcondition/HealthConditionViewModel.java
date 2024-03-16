package com.csc131.deltamedicalteam.ui.healthcondition;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HealthConditionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HealthConditionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Health Condition fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}