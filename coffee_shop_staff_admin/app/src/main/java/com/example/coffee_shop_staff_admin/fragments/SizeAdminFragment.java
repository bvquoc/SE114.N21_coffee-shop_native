package com.example.coffee_shop_staff_admin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffee_shop_staff_admin.activities.SizeAdminDetailActivity;
import com.example.coffee_shop_staff_admin.activities.SizeAdminEditActivity;
import com.example.coffee_shop_staff_admin.adapters.SizeAdminAdapter;
import com.example.coffee_shop_staff_admin.databinding.FragmentSizeAdminBinding;
import com.example.coffee_shop_staff_admin.models.Size;
import com.example.coffee_shop_staff_admin.repositories.SizeRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnSizeAdminClickListener;
import com.example.coffee_shop_staff_admin.viewmodels.SizeAdminViewModel;

import java.util.List;

public class SizeAdminFragment extends Fragment {
    private final SizeAdminAdapter sizeAdminAdapter = new SizeAdminAdapter();
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private final int MILLISECOND_DELAY_SEARCH = 300;
    private final OnSizeAdminClickListener listener = new OnSizeAdminClickListener() {
        @Override
        public void onSizeAdminClickListener(String sizeId) {
            Intent intent = new Intent(getContext(), SizeAdminDetailActivity.class);
            intent.putExtra("sizeId", sizeId);
            activitySeeSizeDetailResultLauncher.launch(intent);
        }
    };
    private final ActivityResultLauncher<Intent> activitySeeSizeDetailResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        //TODO: refresh the page
                    }
                } else {
                    //User do nothing
                }
            }
    );
    private final ActivityResultLauncher<Intent> activityAddNewSizeResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        //TODO: refresh the page
                    }
                } else {
                    //User do nothing
                }
            }
    );
    public SizeAdminFragment() {
        // Required empty public constructor
    }

    public static SizeAdminFragment newInstance() {
        return new SizeAdminFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.example.coffee_shop_staff_admin.databinding.FragmentSizeAdminBinding fragmentSizeAdminBinding = FragmentSizeAdminBinding.inflate(inflater, container, false);

        sizeAdminAdapter.setOnSizeAdminClickListener(listener);
        fragmentSizeAdminBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentSizeAdminBinding.recyclerView.setAdapter(sizeAdminAdapter);

        SizeAdminViewModel sizeAdminViewModel = new SizeAdminViewModel();
        SizeRepository.getInstance().getSizeListMutableLiveData().observe(this, new Observer<List<Size>>() {
            @Override
            public void onChanged(List<Size> sizes) {
                sizeAdminViewModel.setLoading(true);
                if (sizes != null) {
                    sizeAdminAdapter.changeDataSet(sizes);
                    sizeAdminViewModel.setLoading(false);
                }
            }
        });

        fragmentSizeAdminBinding.setViewModel(sizeAdminViewModel);

        fragmentSizeAdminBinding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SizeAdminEditActivity.class);
                activityAddNewSizeResultLauncher.launch(intent);
            }
        });

        fragmentSizeAdminBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        sizeAdminAdapter.getFilter().filter(s);
                    }

                };
                handler.postDelayed(searchRunnable, MILLISECOND_DELAY_SEARCH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return fragmentSizeAdminBinding.getRoot();
    }
}