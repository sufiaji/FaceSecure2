package com.merkaba.facesecure2.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.merkaba.facesecure2.R;
import com.merkaba.facesecure2.activity.MainActivity;

public class BottomSheetFragmentMessage extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        String msgType = getArguments().getString(MainActivity.ARGS_MESSAGE_TYPE);
        if(msgType.equalsIgnoreCase(MainActivity.MESSAGE_PERSON_UNKNOWN)) {
            View view = View.inflate(getContext(), R.layout.fragment_bottom_unknown, null);
            dialog.setContentView(view);
            ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else {
            View view = View.inflate(getContext(), R.layout.fragment_bottom_message, null);
            dialog.setContentView(view);
            ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
            RelativeLayout titleLayout = view.findViewById(R.id.bottom_message_title);
            ImageView ivIcon = view.findViewById(R.id.bottom_message_icon);
            TextView tvMsg = view.findViewById(R.id.bottom_message_content);

            if(msgType.equalsIgnoreCase(MainActivity.MESSAGE_ERROR)) {
                titleLayout.setBackgroundColor(getResources().getColor(R.color.colorRed));
                ivIcon.setImageResource(R.drawable.ic_error);
            } else if(msgType.equalsIgnoreCase(MainActivity.MESSAGE_INFO)) {
                titleLayout.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                ivIcon.setImageResource(R.drawable.ic_about);
            } else if(msgType.equalsIgnoreCase(MainActivity.MESSAGE_SUCCESS)) {
                titleLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivIcon.setImageResource(R.drawable.ic_ok);
            } else if(msgType.equalsIgnoreCase(MainActivity.MESSAGE_WARNING)) {
                titleLayout.setBackgroundColor(getResources().getColor(R.color.colorYellowDark));
                ivIcon.setImageResource(R.drawable.ic_warning);
            }
            String msgContent = getArguments().getString(MainActivity.ARGS_MESSAGE_CONTENT);
            tvMsg.setText(msgContent);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        return  dialog;
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();

        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MainActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(
            new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int i) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
                }
            });
    }
}
