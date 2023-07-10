package com.example.coffee_shop_app.activities.promo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.store.StoreSearchActivity;
import com.example.coffee_shop_app.adapters.PromoAdapter;
import com.example.coffee_shop_app.databinding.ActivityPromoBinding;
import com.example.coffee_shop_app.databinding.ActivityQrcodeScannerBinding;
import com.example.coffee_shop_app.models.Promo;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.PromoRepository;
import com.example.coffee_shop_app.utils.interfaces.OnPromoClickListener;
import com.example.coffee_shop_app.viewmodels.PromoViewModel;
import com.example.coffee_shop_app.viewmodels.StoreViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PromoActivity extends AppCompatActivity {
    private ActivityPromoBinding activityPromoBinding;
    private BottomSheetDialog bottomSheetDialog;
    private PromoAdapter promoAdapter = new PromoAdapter(new ArrayList<>());
    private OnPromoClickListener listener = new OnPromoClickListener() {
        @Override
        public void onPromoClick(Promo promo) {
            bottomSheetDialog = new BottomSheetDialog(PromoActivity.this, R.style.BottomSheetTheme);
            View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.promo_bottom_sheet, null);

            sheetView.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });

            sheetView.findViewById(R.id.use_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    choosePromo(promo);
                }
            });
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            String minPrice = formatter.format(promo.getMinPrice());
            String maxPrice = formatter.format(promo.getMaxPrice());
            ((TextView)sheetView.findViewById(R.id.promo_title)).setText("Giảm " + formatter.format(promo.getPercent() * 100) + "% đơn " + minPrice);

            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            ((TextView)sheetView.findViewById(R.id.promo_date)).setText(dateFormat.format(promo.getDateEnd()));

            ((TextView)sheetView.findViewById(R.id.promo_min_price)).setText(minPrice+"đ");

            ((TextView)sheetView.findViewById(R.id.promo_max_price)).setText(maxPrice+"đ");

            ((TextView)sheetView.findViewById(R.id.promo_description)).setText(promo.getDescription());

            ((TextView)sheetView.findViewById(R.id.promo_code)).setText(promo.getPromoCode());

            int sdp108 = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._108sdp);
            ImageView imageView = sheetView.findViewById(R.id.promo_qr);
            Bitmap qrCodeBitmap = generateQRCode(promo.getPromoCode(), sdp108, sdp108);
            imageView.setImageBitmap(qrCodeBitmap);

            bottomSheetDialog.setContentView(sheetView);
            // Set the behavior to STATE_EXPANDED
            View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
        }
    };

    private ActivityResultLauncher<ScanOptions> activityQRScanResultLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents()!=null) {
                    String promoCode = result.getContents().toString();
                    activityPromoBinding.editText.setText(promoCode);
                } else {
                    //User do nothing
                }
            }
    );

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

    private void choosePromo(Promo promo)
    {
        Toast.makeText(getApplicationContext(), promo.getPromoCode(), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        activityPromoBinding = DataBindingUtil.setContentView(this, R.layout.activity_promo);

        init();
    }

    private void init()
    {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Mã khuyến mãi");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        promoAdapter.setOnPromoClickListener(listener);

        activityPromoBinding.findPromosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityPromoBinding.findPromosRecyclerView.setAdapter(promoAdapter);

        PromoViewModel promoViewModel = new PromoViewModel();
        activityPromoBinding.setViewModel(promoViewModel);

        PromoRepository.getInstance().getPromoListMutableLiveData().observe(this, new Observer<List<Promo>>() {
            @Override
            public void onChanged(List<Promo> promos) {
                promoViewModel.setLoading(true);
                promoAdapter.changeDataSet(promos);
                promoViewModel.setLoading(false);
            }
        });

        activityPromoBinding.applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoViewModel.setSearching(true);
                List<Promo> promos = PromoRepository.getInstance().getPromoListMutableLiveData().getValue();
                for (Promo promo:
                     promos) {
                    if(promo.getPromoCode().equals(activityPromoBinding.editText.getText().toString()))
                    {
                        promoViewModel.setSearching(false);
                        choosePromo(promo);
                        return;
                    }
                }
                promoViewModel.setSearching(false);
                Toast.makeText(getApplicationContext(), "Mã khuyến mãi không tồn tại hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
            }
        });

        activityPromoBinding.editTextFindPromo.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRCodeScanner();
            }
        });
    }
    private void startQRCodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRCodeScannerActivity.class);
        options.setPrompt("");
        activityQRScanResultLauncher.launch(options);
    }
}