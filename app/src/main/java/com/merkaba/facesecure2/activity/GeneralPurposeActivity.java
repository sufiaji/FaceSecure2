package com.merkaba.facesecure2.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.merkaba.facesecure2.R;

import java.io.ByteArrayOutputStream;

public class GeneralPurposeActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generalpurpose);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
    }

    public void onGenerateClick(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aji);
        ByteArrayOutputStream baosfile = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baosfile);
        byte[] byteArrayPhoto = baosfile.toByteArray();
        String encodedFile = Base64.encodeToString(byteArrayPhoto, Base64.DEFAULT);
        if (Python.isStarted()) {
//            Python.start(new AndroidPlatform(this));
            Python python = Python.getInstance();
            PyObject pythonFile = python.getModule("encoder");
            PyObject ret = pythonFile.callAttr("encode", encodedFile);
            float[] enc = ret.toJava(float[].class);
            imageView.setImageBitmap(bitmap);
            textView.setText(ret.toString());
        }
    }

    public void onPythonClick(View view) {
        if (Python.isStarted()) {
//            Python.start(new AndroidPlatform(this));
            Python python = Python.getInstance();
            PyObject pythonFile = python.getModule("helloworldscript");
            PyObject ret = pythonFile.callAttr("helloworld", "jancuk", "jancuk2");
            textView.setText(ret.toString());
        }
    }
}
