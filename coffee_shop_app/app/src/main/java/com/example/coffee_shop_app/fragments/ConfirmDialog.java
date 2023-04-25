package com.example.coffee_shop_app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.coffee_shop_app.R;

public class ConfirmDialog extends AppCompatDialogFragment {
    private final String title;
    private final String content;
    private final View.OnClickListener onClickListenerYesButton;
    private final View.OnClickListener onClickListenerNoButton;

    public ConfirmDialog(
            String title,
            String content,
            View.OnClickListener onClickListenerYesButton,
            View.OnClickListener onClickListenerNoButton) {
        this.title = title;
        this.content = content;
        this.onClickListenerYesButton = onClickListenerYesButton;
        this.onClickListenerNoButton = onClickListenerNoButton;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_dialog, null, false);

        Button noButton = (Button) view.findViewById(R.id.no_button);
        Button yesButton = (Button) view.findViewById(R.id.yes_button);
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        TextView contentTextView = view.findViewById(R.id.content_text_view);

        if(title != null)
        {
            titleTextView.setText(title);
        }

        if(content != null)
        {
            contentTextView.setText(content);
        }

        if(onClickListenerNoButton == null)
        {
            noButton.setOnClickListener(v -> {
                dismiss(); // Close the dialog
            });
        }
        else
        {
            noButton.setOnClickListener(onClickListenerNoButton);
        }

        if(onClickListenerYesButton == null)
        {
            yesButton.setOnClickListener(v -> {
                dismiss(); // Close the dialog
            });
        }
        else
        {
            yesButton.setOnClickListener(onClickListenerNoButton);
        }

        builder.setView(view);

        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Dialog dialog = getDialog();

        Window window = dialog.getWindow();

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 32);

        window.setBackgroundDrawable(inset);

        View view = inflater.inflate(R.layout.confirm_dialog, container, false);

        return view;
    }
}
