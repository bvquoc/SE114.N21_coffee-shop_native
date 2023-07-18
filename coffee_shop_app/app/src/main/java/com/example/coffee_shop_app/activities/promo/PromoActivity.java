package com.example.coffee_shop_app.activities.promo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.adapters.PromoAdapter;
import com.example.coffee_shop_app.databinding.ActivityPromoBinding;
import com.example.coffee_shop_app.fragments.Dialog.NotificationDialog;
import com.example.coffee_shop_app.models.Promo;
import com.example.coffee_shop_app.models.Store;
import com.example.coffee_shop_app.repository.PromoRepository;
import com.example.coffee_shop_app.utils.interfaces.OnPromoClickListener;
import com.example.coffee_shop_app.viewmodels.CartButtonViewModel;
import com.example.coffee_shop_app.viewmodels.CartViewModel;
import com.example.coffee_shop_app.viewmodels.PromoViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PromoActivity extends AppCompatActivity {
    private ActivityPromoBinding activityPromoBinding;
    private BottomSheetDialog bottomSheetDialog;
    private final PromoAdapter promoAdapter = new PromoAdapter(new ArrayList<>());
    private final PromoViewModel promoViewModel = new PromoViewModel();
    private AsyncTask<Void, Void, String> checkPromoTask;
    private final OnPromoClickListener listener = new OnPromoClickListener() {
        @Override
        public void onPromoClick(Promo promo) {
            bottomSheetDialog = new BottomSheetDialog(PromoActivity.this, R.style.BottomSheetTheme);
            View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.promo_bottom_sheet, null);

            sheetView.findViewById(R.id.close_button).setOnClickListener(v -> bottomSheetDialog.dismiss());

            sheetView.findViewById(R.id.use_button).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                if (checkPromoTask != null) {
                    checkPromoTask.cancel(true);
                }
                checkPromoTask = new CheckPromoTask(promo);
                checkPromoTask.execute();
            });
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            String minPrice = formatter.format(promo.getMinPrice());
            String maxPrice = formatter.format(promo.getMaxPrice());
            ((TextView) sheetView.findViewById(R.id.promo_title)).setText("Giảm " + formatter.format(promo.getPercent() * 100) + "% đơn " + minPrice);

            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            ((TextView) sheetView.findViewById(R.id.promo_date)).setText(dateFormat.format(promo.getDateEnd()));

            ((TextView) sheetView.findViewById(R.id.promo_min_price)).setText(minPrice + "đ");

            ((TextView) sheetView.findViewById(R.id.promo_max_price)).setText(maxPrice + "đ");

            ((TextView) sheetView.findViewById(R.id.promo_description)).setText(promo.getDescription());

            ((TextView) sheetView.findViewById(R.id.promo_code)).setText(promo.getPromoCode());

            if(promo.isForNewCustomer())
            {
                ((TextView)sheetView.findViewById(R.id.status_text_view)).setText("Khách hàng mới");
                ((TextView)sheetView.findViewById(R.id.status_text_view)).setTextColor(ContextCompat.getColor(PromoActivity.this, R.color.blue));
                ((CardView)sheetView.findViewById(R.id.status_card_view)).setCardBackgroundColor(ContextCompat.getColor(PromoActivity.this, R.color.blueBackground));
            }
            else
            {
                ((TextView)sheetView.findViewById(R.id.status_text_view)).setText("Tất cả khách hàng");
                ((TextView)sheetView.findViewById(R.id.status_text_view)).setTextColor(ContextCompat.getColor(PromoActivity.this, R.color.grey_text));
                ((CardView)sheetView.findViewById(R.id.status_card_view)).setCardBackgroundColor(ContextCompat.getColor(PromoActivity.this, R.color.grey_border));
            }

            int sdp108 = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._108sdp);
            ImageView imageView = sheetView.findViewById(R.id.promo_qr);
            Bitmap qrCodeBitmap = generateQRCode(promo.getPromoCode(), sdp108, sdp108);
            imageView.setImageBitmap(qrCodeBitmap);

            bottomSheetDialog.setContentView(sheetView);
            // Set the behavior to STATE_EXPANDED
            View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();
            }
        }
    };

    private final ActivityResultLauncher<ScanOptions> activityQRScanResultLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String promoCode = result.getContents();
                    activityPromoBinding.editText.setText(promoCode);
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

    private void choosePromo(Promo promo) {
        //TODO: MAI - return promo
        Toast.makeText(getApplicationContext(), promo.getPromoCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        activityPromoBinding = DataBindingUtil.setContentView(this, R.layout.activity_promo);

        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Mã khuyến mãi");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        promoAdapter.setOnPromoClickListener(listener);

        activityPromoBinding.findPromosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityPromoBinding.findPromosRecyclerView.setAdapter(promoAdapter);

        activityPromoBinding.setViewModel(promoViewModel);

        PromoRepository.getInstance().getPromoListMutableLiveData().observe(this, promos -> {
            promoViewModel.setLoading(true);
            promoAdapter.changeDataSet(promos);
            promoViewModel.setLoading(false);
        });

        activityPromoBinding.loading.setOnTouchListener((v, event) -> true);

        activityPromoBinding.applyButton.setOnClickListener(v -> {
            if (checkPromoTask != null) {
                checkPromoTask.cancel(true);
            }
            checkPromoTask = new CheckPromoTask(activityPromoBinding.editText.getText() == null ? "" : activityPromoBinding.editText.getText().toString());
            checkPromoTask.execute();
        });

        activityPromoBinding.editTextFindPromo.setStartIconOnClickListener(v -> startQRCodeScanner());

        activityPromoBinding.refreshLayout.setOnRefreshListener(() -> {
            PromoRepository.getInstance().registerSnapshotListener();
            activityPromoBinding.refreshLayout.setRefreshing(false);
        });
    }

    private void startQRCodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRScanActivity.class);
        options.setPrompt("");
        activityQRScanResultLauncher.launch(options);
    }

    private final class CheckPromoTask extends AsyncTask<Void, Void, String> {
        private final String selectedPromoId;
        private Promo selectedPromo;

        public CheckPromoTask(String selectedPromoId) {
            this.selectedPromoId = selectedPromoId;
        }

        public CheckPromoTask(Promo selectedPromo) {
            this.selectedPromo = selectedPromo;
            this.selectedPromoId = selectedPromo.getPromoCode();
        }

        @Override
        protected void onPreExecute() {
            promoViewModel.setSearching(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            if(selectedPromo==null)
            {
                List<Promo> promos = PromoRepository.getInstance().getPromoListMutableLiveData().getValue();
                if (promos != null) {
                    for (Promo promo : promos) {
                        if (promo.getPromoCode().equals(selectedPromoId)) {
                            selectedPromo = promo;
                            break;
                        }
                    }
                }
            }
            if (selectedPromo != null) {
                Store selectedStore = CartButtonViewModel.getInstance().getSelectedStore().getValue();
                String storeId = null;
                if (selectedStore != null) {
                    storeId = selectedStore.getId();
                }
                double totalPrice = 0;
                Object totalPriceObject = CartViewModel.getInstance().getTotalFood().getValue();
                if (totalPriceObject != null) {
                    totalPrice = (double) totalPriceObject;
                }
                if (storeId == null) {
                    return "Vui lòng chọn cửa hàng.";
                } else if (!selectedPromo.getStores().contains(storeId)) {
                    return "Mã giảm giá không áp dụng cho cửa hàng này.";
                } else if (selectedPromo.getMinPrice() > totalPrice) {
                    return "Đơn hàng không đạt giá trị tối thiểu.";
                } else {
                    Date now = new Date();
                    if (selectedPromo.getDateEnd().before(now) ||
                            selectedPromo.getDateStart().after(now)) {
                        return "Chưa đến thời điểm áp dụng được mã giảm giá";
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(Data.instance.createAt);

                        calendar.add(Calendar.DAY_OF_YEAR, 7);
                        Date createAtAfter7Days = calendar.getTime();
                        if (createAtAfter7Days.before(now) && selectedPromo.isForNewCustomer()) {
                            return "Mã giảm giá chỉ dành cho khách hàng tạo tài khoản cách đây 7 ngày";
                        } else {
                            return "";
                        }
                    }
                }
            }
            else
            {
                return "Mã khuyến mãi không tồn tại hoặc đã hết hạn";
            }
        }

        @Override
        protected void onPostExecute(String v) {
            if(v.equals(""))
            {
                promoViewModel.setSearching(false);
                choosePromo(selectedPromo);
            }
            else
            {
                NotificationDialog dialog = new NotificationDialog(
                        NotificationDialog.NotificationType.failed,
                        v,
                        null
                );
                promoViewModel.setSearching(false);
                dialog.show(getSupportFragmentManager(), "notificationDialog");
            }
        }
    }
}