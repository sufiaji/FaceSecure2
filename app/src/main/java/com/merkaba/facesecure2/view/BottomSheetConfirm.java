package com.merkaba.facesecure2.view;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.merkaba.facesecure2.R;
import com.merkaba.facesecure2.activity.MainActivity;
import com.merkaba.facesecure2.model.CountDownAnimation;
import com.merkaba.facesecure2.utils.Utils;
import com.ornach.nobobutton.NoboButton;

public class BottomSheetConfirm extends BottomSheetDialogFragment {

    private Bitmap mFace = null;
    private String mId = "";
    private String mNickname = "";
    private boolean mOnline = true;
    private int mSeconder = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.MyBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_attendance_confirm, container,
                false);
        // arguments
        mId = getArguments().getString(MainActivity.ARGS_ID); // userId
        byte[] arrayByte = getArguments().getByteArray(MainActivity.ARGS_BITMAP); // face thumb bitmap
        mFace = Utils.getBitmapFromByteArray(arrayByte); // face thumb bitmap
        mSeconder = getArguments().getInt(MainActivity.ARGS_TIMER_WAITING_VOICE_COMMAND); // timer in second
        mNickname = getArguments().getString(MainActivity.ARGS_NICKNAME); // name
        mOnline = getArguments().getBoolean(MainActivity.ARGS_ONLINE_STATUS);
        // button listener
        NoboButton btnIn = view.findViewById(R.id.button_in);
        NoboButton btnOut = view.findViewById(R.id.button_out);
        NoboButton btnCancel = view.findViewById(R.id.button_cancel);
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onBottomSheetButtonClick(mId, mNickname,
                        MainActivity.STRING_CLOCK_IN, mOnline, mFace);
            }
        });
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onBottomSheetButtonClick(mId, mNickname,
                        MainActivity.STRING_CLOCK_OUT, mOnline, mFace);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onBottomSheetButtonClick(mId, mNickname,
                        MainActivity.STRING_CLOCK_CANCEL, mOnline, mFace);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MainActivity m = (MainActivity) getActivity();
        TextView t = view.findViewById(R.id.text_counter);
        c = new CountDownAnimation(t, mSeconder);
        c.setCountDownListener(new CountDownAnimation.CountDownListener() {
            @Override
            public void onCountDownEnd(CountDownAnimation animation) {
                ((MainActivity) getActivity()).showDebug("Counter done.");
                ((MainActivity) getActivity()).onAttendanceConfirmCounterEnd();
            }
        });
        c.start();
    }

    CountDownAnimation c;

    public void stopCounter() {
        if(c!=null) {
            c.cancel();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        stopCounter();
//        Toast.makeText(getActivity(), "onDismiss!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        stopCounter();
        Toast.makeText(getActivity(), "onCancel!", Toast.LENGTH_SHORT).show();
    }
}
