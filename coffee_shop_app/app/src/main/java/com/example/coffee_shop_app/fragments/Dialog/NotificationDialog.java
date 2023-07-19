package com.example.coffee_shop_app.fragments.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.coffee_shop_app.R;

import java.util.Objects;

public class NotificationDialog  extends AppCompatDialogFragment {
    private final NotificationType notificationType;
    private final String title;
    private final String content;
    private final DialogInterface.OnDismissListener dismissListener;
    public NotificationDialog(
            NotificationType notificationType,
            String content,
            DialogInterface.OnDismissListener dismissListener) {
        this.notificationType = notificationType;
        if(notificationType == NotificationType.success)
        {
            this.title = "Thành công";
        }
        else
        {
            this.title = "Thất bại";
        }
        this.content = content;
        this.dismissListener = dismissListener;
    }
    public NotificationDialog(
            NotificationType notificationType,
            String title,
            String content,
            DialogInterface.OnDismissListener dismissListener) {
        this.notificationType = notificationType;
        this.title = title;
        this.content = content;
        this.dismissListener = dismissListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.notification_dialog, null, false);

        Button okButton = (Button) view.findViewById(R.id.ok_button);
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        TextView contentTextView = view.findViewById(R.id.content_text_view);
        ImageView imageView = (ImageView)view.findViewById(R.id.img_status);

        if(title != null)
        {
            titleTextView.setText(title);
        }

        if(content != null)
        {
            contentTextView.setText(content);
        }

        okButton.setOnClickListener(v -> dismiss());

        if(notificationType == NotificationType.success)
        {
            imageView.setImageResource(R.drawable.img_success_dialog);
        }
        else
        {
            imageView.setImageResource(R.drawable.img_failed_dialog);
        }

        builder.setView(view);

        Dialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Dialog dialog = getDialog();

        if(dialog!=null)
        {
            Window window = dialog.getWindow();

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 120);

            window.setBackgroundDrawable(inset);
        }

        return null;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dismissListener!=null)
        {
            dismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog()).setCancelable(false);
    }

    public enum NotificationType{
        success,
        failed
    }
}
