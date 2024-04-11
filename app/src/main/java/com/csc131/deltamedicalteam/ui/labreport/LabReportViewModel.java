package com.csc131.deltamedicalteam.ui.labreport;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LabReportViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LabReportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Lab Report fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}