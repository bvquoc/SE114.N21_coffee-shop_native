package com.example.coffee_shop_staff_admin.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.coffee_shop_staff_admin.R;
import com.example.coffee_shop_staff_admin.adapters.PromoAdminStoreAdapter;
import com.example.coffee_shop_staff_admin.databinding.ActivityPromoAdminDetailBinding;
import com.example.coffee_shop_staff_admin.fragments.ConfirmDialog;
import com.example.coffee_shop_staff_admin.models.Promo;
import com.example.coffee_shop_staff_admin.repositories.PromoRepository;
import com.example.coffee_shop_staff_admin.repositories.StoreRepository;
import com.example.coffee_shop_staff_admin.utils.interfaces.OnPromoAdminStoreCheckedChangedListener;
import com.example.coffee_shop_staff_admin.viewmodels.PromoAdminDetailViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PromoAdminDetailActivity extends AppCompatActivity {
    private final String TAG = "PromoAdminDetailActivity";
    private ActivityPromoAdminDetailBinding activityPromoAdminDetailBinding;
    private final PromoAdminDetailViewModel promoAdminDetailViewModel = new PromoAdminDetailViewModel();
    private final PromoAdminStoreAdapter promoAdminStoreAdapter = new PromoAdminStoreAdapter(false);
    private String promoId;
    private final List<String> storeIds = new ArrayList<>();
    private AsyncTask<Void, Void, Void> deletePromoTask;
    private Promo selectedPromo;

    private final OnPromoAdminStoreCheckedChangedListener listener = (storeId, isChecked) -> {
        if(!isChecked)
        {
            storeIds.remove(storeId);
        }
        else
        {
            storeIds.add(storeId);
        }
    };

    private final ActivityResultLauncher<Intent> activityEditPromoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    finish();
                } else {
                    //User do nothing
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityPromoAdminDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_promo_admin_detail);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Chi tiết mã giảm giá");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        promoId = intent.getStringExtra("promoId");
        init(promoId);
    }
    private Bitmap generateQRCode(String data, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void init(String promoId)
    {
        promoAdminStoreAdapter.setOnPromoAdminStoreCheckedChangedListener(listener);
        PromoRepository.getInstance().getPromoListMutableLiveData().observe(this, promos -> {
            promoAdminDetailViewModel.setLoadingPromo(true);
            if(promos!=null)
            {
                selectedPromo = null;
                for (Promo promo: promos) {
                    if(promo.getPromoCode().equals(promoId))
                    {
                        selectedPromo = promo;
                        break;
                    }
                }
                if(selectedPromo!=null)
                {
                    int sdp108 = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._108sdp);
                    Bitmap qrCodeBitmap = generateQRCode(selectedPromo.getPromoCode(), sdp108, sdp108);
                    activityPromoAdminDetailBinding.promoQr.setImageBitmap(qrCodeBitmap);

                    promoAdminStoreAdapter.changeAvailableStores(selectedPromo.getStores());

                    promoAdminDetailViewModel.setCode(selectedPromo.getPromoCode());

                    promoAdminDetailViewModel.setDescription(selectedPromo.getDescription());

                    DecimalFormat formatter = new DecimalFormat("#,##0.##");
                    String minPrice = formatter.format(selectedPromo.getMinPrice());
                    promoAdminDetailViewModel.setMinPrice(minPrice+"đ");

                    String maxPrice = formatter.format(selectedPromo.getMaxPrice());
                    promoAdminDetailViewModel.setMaxPrice(maxPrice+"đ");

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    promoAdminDetailViewModel.setDateStart(dateFormat.format(selectedPromo.getDateStart()));

                    promoAdminDetailViewModel.setDateEnd(dateFormat.format(selectedPromo.getDateEnd()));

                    promoAdminDetailViewModel.setForNewCustomer(selectedPromo.isForNewCustomer());

                    promoAdminDetailViewModel.setCanEdit(selectedPromo.getDateStart().after(new Date()));

                    promoAdminDetailViewModel.setLoadingPromo(false);
                }
            }
        });
        StoreRepository.getInstance().getStoreListMutableLiveData().observe(this, stores -> {
            promoAdminDetailViewModel.setLoadingStore(true);
            if(stores != null)
            {
                promoAdminStoreAdapter.changeDataSet(stores);
                promoAdminDetailViewModel.setLoadingStore(false);
            }
        });

        activityPromoAdminDetailBinding.setViewModel(promoAdminDetailViewModel);

        activityPromoAdminDetailBinding.recyclerStore.setLayoutManager(new LinearLayoutManager(this));
        activityPromoAdminDetailBinding.recyclerStore.setAdapter(promoAdminStoreAdapter);

        activityPromoAdminDetailBinding.editButton.setOnClickListener(v -> {

        });
        activityPromoAdminDetailBinding.deleteButton.setOnClickListener(v -> {
            ConfirmDialog dialog = new ConfirmDialog(
                    "Thông báo",
                    "Bạn có chắc muốn xóa mã giảm giá này",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(deletePromoTask!=null)
                            {
                                deletePromoTask.cancel(true);
                            }
                            deletePromoTask = new DeletePromoTask();
                            deletePromoTask.execute();
                        }
                    },
                    null
            );
            dialog.show(getSupportFragmentManager(), "confirmDialog");
        });
        activityPromoAdminDetailBinding.editButton.setOnClickListener(v -> {
            if(selectedPromo!=null)
            {
                Intent intent = new Intent(PromoAdminDetailActivity.this, PromoAdminEditActivity.class);
                intent.putExtra("promoId", selectedPromo.getPromoCode());
                intent.putExtra("description", selectedPromo.getDescription());
                intent.putExtra("minPrice", selectedPromo.getMinPrice());
                intent.putExtra("maxPrice", selectedPromo.getMaxPrice());
                intent.putExtra("percent", selectedPromo.getPercent());
                intent.putExtra("startDate", selectedPromo.getDateStart());
                intent.putExtra("closeDate", selectedPromo.getDateEnd());
                intent.putExtra("isForNewCustomer", selectedPromo.isForNewCustomer());
                intent.putStringArrayListExtra("stores", new ArrayList<>(selectedPromo.getStores()));
                activityEditPromoResultLauncher.launch(intent);
            }
        });
    }
    private final class DeletePromoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            promoAdminDetailViewModel.setUpdating(true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            PromoRepository.getInstance().deletePromo(promoId, success -> {
                if(success)
                {
                    Log.e(TAG, "delete promo successfully.");
                    promoAdminDetailViewModel.setUpdating(false);
                    runOnUiThread(() -> Toast.makeText(
                            PromoAdminDetailActivity.this,
                            "Đã xóa mã giảm giá thành công.",
                            Toast.LENGTH_SHORT
                    ).show());
                    finish();
                }
                else
                {
                    Log.e(TAG, "update promo failed.");
                    promoAdminDetailViewModel.setUpdating(false);
                    runOnUiThread(() -> Toast.makeText(
                            PromoAdminDetailActivity.this,
                            "Đã có lỗi xảy ra. Xin hãy thử lại sau.",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }
}