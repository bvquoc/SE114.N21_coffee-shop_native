package com.example.coffee_shop_app.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.security.AuthProvider;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imageViewer;
    Button btnImgViewed;
    Button btnImgCancel;
    View separator;
    String cate = "";
    String src = "";

    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Toolbar toolbar = findViewById(R.id.my_toolbar);

        toolbar.setTitle("Xem ảnh");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageViewer = findViewById(R.id.img_viewer);
        btnImgViewed = findViewById(R.id.btn_img_viewed);
        btnImgCancel = findViewById(R.id.btn_img_cancel);
        separator = findViewById(R.id.ctn_separator);

        Bundle extras = getIntent().getExtras();
        String type = "";
        if(extras != null){
            src = extras.getString("src");
            type = extras.getString("type");
            try {
                cate = extras.getString("cate");
            } catch (Exception e){
                Log.d("Image Viewer", "no cate");
            }
        }

        if(type.equals("view")) {
            Picasso.get()
                    .load(src)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .fit()
                    .into(imageViewer);
            btnImgViewed.setOnClickListener(v -> {
                onBackPressed();
            });
        } else {
            Log.d("lel", src);
            separator.setVisibility(View.VISIBLE);
            btnImgCancel.setVisibility(View.VISIBLE);
            imageViewer.setImageURI(Uri.parse(src));
            btnImgViewed.setOnClickListener(v -> {
                onSaveImage(Uri.parse(src));
                onBackPressed();
            });
            btnImgCancel.setOnClickListener(v -> {
                onBackPressed();
            });
        }


    }

    protected  void onSaveImage(Uri uri){
        User temp = AuthRepository.getInstance().getCurrentUser();
        StorageReference ref = storage.getReference();
        String path = "users/" + temp.getId() + "/" + cate + "/" + uri.getLastPathSegment();
        StorageReference newRef = ref.child(path);
        newRef.putFile(uri).addOnSuccessListener(v -> {
            newRef.getDownloadUrl().addOnSuccessListener(e -> {
                if(cate.equals("avatar")){
                    temp.setAvatarUrl(e.toString());
                }
                else {
                    temp.setCoverUrl(e.toString());
                }
                AuthRepository.getInstance().update(temp, params -> {
                    //do nothing
                }, params -> {
                    //do nothing
                });
            }).addOnFailureListener(e -> {
                Log.d("Image viewer", "save image failed");
            });
        }).addOnFailureListener(v -> {
            Log.d("Image viewer", "save image failed");
        });
    }

}