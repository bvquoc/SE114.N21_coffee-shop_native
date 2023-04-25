package com.example.coffee_shop_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.coffee_shop_app.fragments.ConfirmDialog;
import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;

public class EditDeliveryAddressActivity extends AppCompatActivity
    implements View.OnFocusChangeListener{
    private EditText addressEditText;
    private EditText nameEditText;
    private EditText phoneEditText;

    private AutoCompleteTextView cityAutoCompleteTextView;
    private AutoCompleteTextView districtAutoCompleteTextView;
    private AutoCompleteTextView wardAutoCompleteTextView;

    private Toolbar toolbar;
    private Button saveButton;
    private NestedScrollView scrollView;
    int index = -1;
    private long startClickTime;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_address_page, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_delete:
                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmation",
                        "Do you want to remove this address?",
                        null,
                        null
                );
                dialog.show(getSupportFragmentManager(), "confirmDialog");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if ( v instanceof EditText) {
//                Rect outRect = new Rect();
//                v.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
//                    v.clearFocus();
//                    tempView.requestFocusFromTouch();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        }
//        return super.dispatchTouchEvent( event );
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delivery_address);



        index = getIntent().getIntExtra("index", -1);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        toolbar = findViewById(R.id.my_toolbar);
        addressEditText = findViewById(R.id.address_edit_text);
        nameEditText = findViewById(R.id.name_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        saveButton = findViewById(R.id.save_button);
        cityAutoCompleteTextView = findViewById(R.id.city_auto_complete_text_view);
        districtAutoCompleteTextView = findViewById(R.id.district_auto_complete_text_view);
        wardAutoCompleteTextView = findViewById(R.id.ward_auto_complete_text_view);
        scrollView = findViewById(R.id.scroll_view);

        ArrayAdapter<CharSequence> autoCompleteTextViewAdapter = ArrayAdapter.createFromResource(
                this, R.array.autocomplete_temp_items, R.layout.address_autocomplete_item);

        cityAutoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
        cityAutoCompleteTextView.setOnFocusChangeListener(this);
        cityAutoCompleteTextView.setDropDownVerticalOffset(-cityAutoCompleteTextView.getHeight() - 5);

        districtAutoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
        districtAutoCompleteTextView.setOnFocusChangeListener(this);
        districtAutoCompleteTextView.setDropDownVerticalOffset(-districtAutoCompleteTextView.getHeight() - 5);

        wardAutoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
        wardAutoCompleteTextView.setOnFocusChangeListener(this);
        wardAutoCompleteTextView.setDropDownVerticalOffset(-wardAutoCompleteTextView.getHeight() - 5);

        if(index != -1)
        {
            addressEditText.setText(Data.instance.addressDeliveries.get(index).getAddress().getSubAddress());
            nameEditText.setText(Data.instance.addressDeliveries.get(index).getNameReceiver());
            phoneEditText.setText(Data.instance.addressDeliveries.get(index).getPhone());
            toolbar.setTitle("Edit address");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        else
        {
            toolbar.setTitle("New address");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, v.getTop());

                        ((AutoCompleteTextView) v).showDropDown();
                    }
                });
            }
        }
    }
}