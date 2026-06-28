package com.example.biruse_kolco.trjwm.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.biruse_kolco.R;

public class ImageViewerDialogFragment extends DialogFragment {
    private static final String ARG_RES_NAME = "res_name";

    public static ImageViewerDialogFragment newInstance(String resName) {
        ImageViewerDialogFragment fragment = new ImageViewerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RES_NAME, resName);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_image_viewer, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ZoomableImageView image = view.findViewById(R.id.viewerImage);
        ImageView close = view.findViewById(R.id.btnCloseViewer);

        String resName = getArguments() != null ? getArguments().getString(ARG_RES_NAME) : null;
        int resId = resName == null ? 0 : requireContext().getResources().getIdentifier(
                resName, "drawable", requireContext().getPackageName());

        if (resId != 0) {
            image.setImageResource(resId);
        } else {
            image.setImageResource(R.drawable.ic_launcher_foreground);
        }

        close.setOnClickListener(v -> dismissAllowingStateLoss());
    }
}
