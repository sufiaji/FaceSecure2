package com.merkaba.facesecure2.view;

import android.app.Dialog;
import android.content.Context;
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
import com.merkaba.facesecure2.model.CountDownAnimation;
import com.merkaba.facesecure2.utils.Utils;
import com.ornach.nobobutton.NoboButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BottomSheetFragmentConfirm extends BottomSheetDialogFragment {

    private CountDownAnimation countDownAnimation;
    private Bitmap mFace = null;
    private String mId = "";
    private String mNickname = "";
    private boolean mOnline = true;
    private int mSeconder = 10;


    @Override
    public void setupDialog(Dialog dialog, int style) {
        View view = View.inflate(getContext(), R.layout.fragment_bottom_confirm, null);
        dialog.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        NoboButton btnIn = view.findViewById(R.id.button_in);
        NoboButton btnOut = view.findViewById(R.id.button_out);
        NoboButton btnCancel = view.findViewById(R.id.button_cancel);
        // button In
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((MainActivity) getActivity()).onBottomSheetButtonClick(mId, mNickname,
//                        MainActivity.STRING_CLOCK_IN, mOnline, mFace);
//                dismiss();
            }
        });
        // button Out
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((MainActivity) getActivity()).onBottomSheetButtonClick(mId, mNickname,
//                        MainActivity.STRING_CLOCK_OUT, mOnline, mFace);
//                dismiss();
            }
        });
        // button Cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((MainActivity) getActivity()).onBottomSheetButtonClick(mId, mNickname,
//                        MainActivity.STRING_CLOCK_CANCEL, mOnline, mFace);
//                dismiss();
            }
        });

        TextView t = view.findViewById(R.id.text_counter);
        countDownAnimation = new CountDownAnimation(t, mSeconder);
        countDownAnimation.setCountDownListener(new CountDownAnimation.CountDownListener() {
            @Override
            public void onCountDownEnd(CountDownAnimation animation) {
                ((MainActivity) getActivity()).showDebug("Counter done.");
                ((MainActivity) getActivity()).onAttendanceConfirmCounterEnd();
                dismiss();
            }
        });
        //
        mId = getArguments().getString(MainActivity.ARGS_ID); // userId
        byte[] arrayByte = getArguments().getByteArray(MainActivity.ARGS_BITMAP); // face thumb bitmap
        mFace = Utils.byteArrayToBitmap(arrayByte); // face thumb bitmap
        mSeconder = getArguments().getInt(MainActivity.ARGS_TIMER_WAITING_VOICE_COMMAND); // timer in second
        mNickname = getArguments().getString(MainActivity.ARGS_NICKNAME); // name
        mOnline = getArguments().getBoolean(MainActivity.ARGS_ONLINE_STATUS);
        //
        TextView textViewName = view.findViewById(R.id.tv_name);
        textViewName.setText(mNickname);
        TextView textViewId = view.findViewById(R.id.tv_nik);
        textViewId.setText(mId);

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
        textViewTime.setText(textTime + " WIB");

        CompoundIconTextView textViewOffline = view.findViewById(R.id.tv_offline);
        if(mOnline) {
            textViewOffline.setText("Online");
            textViewOffline.setVectorDrawableLeft(R.drawable.ic_circle_green);

        } else {
            textViewOffline.setText("Offline");
            textViewOffline.setVectorDrawableLeft(R.drawable.ic_circle_red);
        }
        CircleImage ci = view.findViewById(R.id.iv_thumb);
        ci.setImageBitmap(mFace);
        countDownAnimation.start();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
                Context c = getContext();
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
//        ((MainActivity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void stopCounter1() {
        if(countDownAnimation !=null) {
            countDownAnimation.cancel();
        }
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
//        stopCounter();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
//        stopCounter();
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
