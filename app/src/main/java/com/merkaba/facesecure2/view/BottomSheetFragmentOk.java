package com.merkaba.facesecure2.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.aakira.compoundicontextview.CompoundIconTextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jackandphantom.circularimageview.CircleImage;
import com.merkaba.facesecure2.R;
import com.merkaba.facesecure2.activity.MainActivity;
import com.merkaba.facesecure2.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BottomSheetFragmentOk extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View view = View.inflate(getContext(), R.layout.fragment_bottom_ok, null);
        dialog.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        //1
        String atype = getArguments().getString(MainActivity.ARGS_ATYPE);
        TextView tvGreet = view.findViewById(R.id.tv_greet);
        if(atype.equalsIgnoreCase(MainActivity.STRING_CLOCK_IN)) {
            tvGreet.setText(getString(R.string.s_welcome));
        } else if(atype.equalsIgnoreCase(MainActivity.STRING_CLOCK_OUT)) {
            tvGreet.setText(getString(R.string.s_see_u));
        } else if(atype.equalsIgnoreCase(MainActivity.STRING_NEW_PERSON)) {
            tvGreet.setText(getString(R.string.success_register));
        } else if(atype.equalsIgnoreCase(MainActivity.STRING_UPDATE_PERSON)) {
            tvGreet.setText(getString(R.string.success_update));
        }
        //2
        String name = getArguments().getString(MainActivity.ARGS_NICKNAME);
        TextView tvName = view.findViewById(R.id.tv_name);
        tvName.setText(name);
        //3
        String nik = getArguments().getString(MainActivity.ARGS_ID);
        TextView tvNik = view.findViewById(R.id.tv_nik);
        tvNik.setText(nik);
        //4
        boolean online = getArguments().getBoolean(MainActivity.ARGS_ONLINE_STATUS);
        CompoundIconTextView textViewOffline = view.findViewById(R.id.tv_offline);
        CompoundIconTextView textViewOnline = view.findViewById(R.id.tv_online);
        if(online) {
            textViewOffline.setVisibility(View.GONE);
            textViewOnline.setVisibility(View.VISIBLE);
        } else {
            textViewOffline.setVisibility(View.VISIBLE);
            textViewOnline.setVisibility(View.GONE);
        }
        //5
        byte[] arrayByte = getArguments().getByteArray(MainActivity.ARGS_BITMAP); // face thumb bitmap
        Bitmap face = Utils.byteArrayToBitmap(arrayByte); // face thumb bitmap
        CircleImage ci = view.findViewById(R.id.iv_thumb);
        ci.setImageBitmap(face);

        TextView textViewDate = view.findViewById(R.id.tv_date);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        Date d = new Date();
        String formattedCurrentDate = df.format(d);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(d);
        textViewDate.setText(dayOfTheWeek + ", " + formattedCurrentDate);
        TextView textViewTime = view.findViewById(R.id.tv_time);
        SimpleDateFormat sdft = new SimpleDateFormat("HH:mm");
        String textTime = sdft.format(d);
        textViewTime.setText(textTime);
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
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
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
        float j = (float) windowHeight;
        j = j*0.8f;
        int i = Math.round(j);
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((MainActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
                    }
                });
    }

}
