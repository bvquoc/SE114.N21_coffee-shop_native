package com.example.coffee_shop_app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.coffee_shop_app.databinding.FragmentTimePickerBottomSheetBinding;
import com.example.coffee_shop_app.viewmodels.CartPickupViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TimePickerBottomSheet extends BottomSheetDialogFragment {
    private CartPickupViewModel viewModel;
    private FragmentTimePickerBottomSheetBinding timePickerBottomSheetBinding;
    // index in array
    private int selectedDayIndex;

    // the hour time value
    private int selectedTimeIndex;
    public TimePickerBottomSheet() {
        // Required empty public constructor
    }
    public TimePickerBottomSheet(CartPickupViewModel viewModel){
        this.viewModel=viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        timePickerBottomSheetBinding = FragmentTimePickerBottomSheetBinding.inflate(inflater, container, false);
        return timePickerBottomSheetBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timePickerBottomSheetBinding.setViewModel(viewModel);
        timePickerBottomSheetBinding.np.setWrapSelectorWheel(false);
        timePickerBottomSheetBinding.np2.setWrapSelectorWheel(false);
        timePickerBottomSheetBinding.btnClosePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        timePickerBottomSheetBinding.np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedTimeIndex=newVal;
                Log.d("TIME", selectedTimeIndex+"..........");
            }
        });
        int selectedDay=timePickerBottomSheetBinding.np.getValue();
        int minValue=viewModel.getHourStartTimeList().getValue().get(selectedDay);
        timePickerBottomSheetBinding.np2
                .setMinValue(minValue);
        selectedTimeIndex=minValue;

        initNumberPicker();
        int timeCloseIndex=viewModel.dateTimeToHour(viewModel.getStorePickup().getValue().getTimeClose());
        viewModel.getHourStartTimeList().observe(getViewLifecycleOwner(), new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> integers) {
                int selectedDay=timePickerBottomSheetBinding.np.getValue();
                int minValue=viewModel.getHourStartTimeList().getValue().get(selectedDay);
                timePickerBottomSheetBinding.np2
                        .setMinValue(minValue);
                timePickerBottomSheetBinding.np2.setDisplayedValues(viewModel.timeListToArray(minValue, timeCloseIndex));
            }
        });
        timePickerBottomSheetBinding.np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedDayIndex=newVal;
                timePickerBottomSheetBinding.np2.setDisplayedValues(null);
                int minValue=viewModel.getHourStartTimeList().getValue().get(newVal);
                timePickerBottomSheetBinding.np2.setMinValue(minValue);
                timePickerBottomSheetBinding.np2.setValue(minValue);
                selectedTimeIndex=minValue;
                timePickerBottomSheetBinding.np2.setDisplayedValues(viewModel.timeListToArray(minValue, timeCloseIndex));
            }
        });

        timePickerBottomSheetBinding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date= viewModel.getDayList().getValue().get(selectedDayIndex);
                int hour=selectedTimeIndex/2;
                int minute=(selectedTimeIndex%2)*30;
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                viewModel.getTimePickup().setValue(c.getTime());
                dismiss();
            }
        });
    }
    public void initNumberPicker(){
        int timeCloseIndex=viewModel.dateTimeToHour(viewModel.getStorePickup().getValue().getTimeClose());
        int minValueDay=0;
        int maxValueDay=viewModel.getDayList().getValue().size()-1;
        timePickerBottomSheetBinding.np.setMinValue(minValueDay);
        timePickerBottomSheetBinding.np.setMaxValue(maxValueDay);
        timePickerBottomSheetBinding.np.setDisplayedValues(viewModel.dayListToArray());
        int minValueTime=viewModel.getHourStartTimeList().getValue().get(0);
        timePickerBottomSheetBinding.np2
                .setMinValue(minValueTime);
        timePickerBottomSheetBinding.np2
                .setMaxValue(timeCloseIndex);
    }
}
