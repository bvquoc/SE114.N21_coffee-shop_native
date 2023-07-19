package com.example.coffee_shop_app.fragments.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coffee_shop_app.R;
import com.example.coffee_shop_app.activities.AuthActivity;
import com.example.coffee_shop_app.activities.profile.ImageViewActivity;
import com.example.coffee_shop_app.activities.profile.ProfileSettingActivity;
import com.example.coffee_shop_app.databinding.FragmentProfileBinding;
import com.example.coffee_shop_app.fragments.auth.SignUpFragment;
import com.example.coffee_shop_app.models.User;
import com.example.coffee_shop_app.repository.AuthRepository;
import com.example.coffee_shop_app.viewmodels.ProfileSettingViewModel;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {


    FragmentProfileBinding fragmentProfileBinding;
    ImageView imgAvatar, imgCover;
    TextView nameText;

    MutableLiveData<User> currentUser;

    ProfileSettingViewModel viewModel;

    ActivityResultLauncher<PickVisualMediaRequest> coverLauncher, avaLaucher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = AuthRepository.getInstance().getCurrentUserLiveData();
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileSettingViewModel.class);
        createImageResultLauncher();
        setToolBarTitle("Trang cá nhân");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setButtonListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        fragmentProfileBinding.setLifecycleOwner(this);


        imgAvatar = fragmentProfileBinding.imgAvatar;
        imgCover = fragmentProfileBinding.imgCover;
        nameText = fragmentProfileBinding.txtNameProfile;

        Picasso.get()
                .load(currentUser.getValue().getAvatarUrl())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .fit()
                .into(imgAvatar);

        Picasso.get()
                .load(currentUser.getValue().getCoverUrl())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .fit()
                .centerCrop()
                .into(imgCover);

        nameText.setText(currentUser.getValue().getName());

        currentUser.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Picasso.get()
                        .load(user.getAvatarUrl())
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                        .fit()
                        .into(imgAvatar);

                Picasso.get()
                        .load(user.getCoverUrl())
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                        .fit()
                        .centerCrop()
                        .into(imgCover);
                nameText.setText(user.getName());
            }
        });


        // Inflate the layout for this fragment
        return fragmentProfileBinding.getRoot();
    }

    public void setToolBarTitle(String title) {
        Toolbar toolbar = ((AppCompatActivity) requireActivity()).findViewById(R.id.my_toolbar);
        toolbar.setTitle(title);
    }

    public void onGoOrder(View view) {
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNavView);
        bottomNav.setSelectedItemId(R.id.orderFragment);
    }

    public void onGoSupport(View view) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.support_dialog);

        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.close_button);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onGoSettings(View view) {
        Intent intent = new Intent(getContext(), ProfileSettingActivity.class);
        startActivity(intent);
    }

    public void onLogout(View view) {
        NavHostFragment.findNavController(this).popBackStack();
        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.putExtra("GO_LOGIN", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        AuthRepository.getInstance().signOut();
    }

    private void setButtonListener() {
        fragmentProfileBinding.btnGotoOrder.btnProfileFunction.setOnClickListener((v) -> {
            onGoOrder(v);
        });

        fragmentProfileBinding.btnGotoSupport.btnProfileFunction.setOnClickListener((view -> {
            onGoSupport(view);
        }));

        fragmentProfileBinding.btnGotoSettings.btnProfileFunction.setOnClickListener((v) -> {
            onGoSettings(v);
        });

        fragmentProfileBinding.btnLogout.btnProfileFunction.setOnClickListener((v) -> {
            onLogout(v);
        });

        fragmentProfileBinding.btnChangeCover.setOnClickListener(v -> {
            final CharSequence[] items = {"Xem ảnh", "Thay đổi ảnh bìa"};
            Context context = getContext();
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Chọn ảnh")
                    .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0:
                                            Intent intent = new Intent(getContext(), ImageViewActivity.class);
                                            intent.putExtra("src", currentUser.getValue().getCoverUrl());
                                            intent.putExtra("type", "view");
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            onOpenPickImage(true);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                    ).show();
        });

        fragmentProfileBinding.containerAvatar.setOnClickListener(v -> {
            final CharSequence[] items = {"Xem ảnh", "Thay đổi ảnh đại diện"};
            Context context = getContext();
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Chọn ảnh")
                    .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0:
                                            Intent intent = new Intent(getContext(), ImageViewActivity.class);
                                            intent.putExtra("src", currentUser.getValue().getAvatarUrl());
                                            intent.putExtra("type", "view");
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            onOpenPickImage(false);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                    ).show();
        });
    }

    private void createImageResultLauncher() {
        coverLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                Intent intent = new Intent(getContext(), ImageViewActivity.class);
                intent.putExtra("src", uri.toString());
                intent.putExtra("type", "change");
                intent.putExtra("cate", "cover");
                startActivity(intent);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
        avaLaucher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                Intent intent = new Intent(getContext(), ImageViewActivity.class);
                intent.putExtra("src", uri.toString());
                intent.putExtra("type", "change");
                intent.putExtra("cate", "avatar");
                startActivity(intent);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
    }

    private void onOpenPickImage(Boolean type) {
        if (type) {
            coverLauncher.launch(new PickVisualMediaRequest.Builder().build());
        } else {
            avaLaucher.launch(new PickVisualMediaRequest.Builder().build());
        }
    }
}