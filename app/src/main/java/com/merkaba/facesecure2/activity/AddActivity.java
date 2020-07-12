package com.merkaba.facesecure2.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.formats.proto.DetectionProto;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.glutil.EglManager;
import com.jackandphantom.circularimageview.CircleImage;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.merkaba.facesecure2.R;
import com.merkaba.facesecure2.model.Encoding;
import com.merkaba.facesecure2.model.OneCameraXPreviewHelper;
import com.merkaba.facesecure2.model.User;
import com.merkaba.facesecure2.utils.DatabaseHelper;
import com.merkaba.facesecure2.utils.DateUtils;
import com.merkaba.facesecure2.utils.Utils;
import com.merkaba.facesecure2.view.BottomSheetFragmentOk;
import com.pixplicity.easyprefs.library.Prefs;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
    // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
    // This is needed because OpenGL represents images assuming the image origin is at the bottom-left
    // corner, whereas MediaPipe in general assumes the image origin is at top-left.
    private static final boolean FLIP_FRAMES_VERTICALLY = true;

    static {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private SurfaceTexture mPreviewFrameTexture;

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private SurfaceView mPreviewDisplayView;

    // Creates and manages an {@link EGLContext}.
    private EglManager mEglManager;

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    private FrameProcessor mProcessor;

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private ExternalTextureConverter mConverter;

    // Handles camera access via the {@link CameraX} Jetpack support library.
    private OneCameraXPreviewHelper mCameraHelper;

    // no concurrent frame processing
    boolean mIsProcessing = false;

    // size of tensorflow input
    private Size mTensorImageSize;
    private int mTensorImageSize2 = 640;
    private CircleImage mMiniPreview;
    //
    private static final String NA = "na";
    private static final String THREAD_DESTROYED = "destroyed";
    private static final String THREAD_CREATED = "created";
    private static final String THREAD_RUN = "running";

    private Bitmap mCroppedBitmap = null;
//    private Bitmap mCroppedBitmap2 = null;
    private Bitmap mOriginalBitmap = null;
//    private Bitmap mOriginalBitmap2 = null;
    private HandlerThread mHandlerThreadMain;
//    private String mThreadMainStatus = "NULL";
    private Handler mHandlerMain;

//    private final Interpreter.Options mTfliteOptions = new Interpreter.Options();
//    private GpuDelegate mGpuDelegate = null;
//    private MappedByteBuffer mTfliteModel;
//    protected Interpreter mTfliteInterpreter;
//    private GoogleProgressBar mProgressBar;
//    private SpinKitView mProgressBar;
    private ProgressBar mProgressBar;
    private boolean mDebug = true;
    private Context mContext;
    private DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //
        hideNavbar();
        //
        setContentView(R.layout.activity_add);
        mContext = this;
        mMiniPreview = findViewById(R.id.face_preview);
        mProgressBar = findViewById(R.id.progress_bar_add);
        mProgressBar.setVisibility(View.GONE);
        //
        AndroidAssetUtil.initializeNativeAssetManager(this);
        //
        mDbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        //
        createThread();
        //
        mPreviewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();
        mEglManager = new EglManager(null);
        mProcessor =
                new FrameProcessor(
                        this,
                        mEglManager.getNativeContext(),
                        MainActivity.BINARY_GRAPH_NAME,
                        MainActivity.INPUT_VIDEO_STREAM_NAME,
                        MainActivity.OUTPUT_VIDEO_STREAM_NAME);
        mProcessor.getVideoSurfaceOutput().setFlipY(FLIP_FRAMES_VERTICALLY);
        mProcessor.addPacketCallback(
                MainActivity.OUTPUT_DETECTIONS_STREAM_NAME,
                (packet) -> {
                    List<DetectionProto.Detection> detections = PacketGetter.getProtoVector(packet, DetectionProto.Detection.parser());
                    if(detections.size()>0) {
                        Log.d(TAG, "Face(s) detected");
                        if(!mIsProcessing) {
                            processFrame(detections);
                        }
                    }
                });
        mConverter = new ExternalTextureConverter(mEglManager.getContext());
        mConverter.setFlipY(FLIP_FRAMES_VERTICALLY);
        mConverter.setConsumer(mProcessor);
        startCamera();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        destroyThread();
        super.onPause();
        destroyCamera();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            hideNavbar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDbHelper.closeDB();
        finish();
    }

    private void hideNavbar() {
        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void destroyCamera() {
        mConverter.close();
        mConverter = null;
        mCameraHelper.unBind();
        mCameraHelper = null;
        mPreviewDisplayView = null;
    }

    private void destroyThread() {
        Log.d(TAG, "Thread destroyed");
        mHandlerThreadMain.quitSafely();
        try {
            mHandlerThreadMain.join();
            mHandlerThreadMain = null;
            mHandlerMain = null;
//            mThreadMainStatus = THREAD_DESTROYED;
        } catch (final InterruptedException e) {
            Log.e(TAG, "InterruptedException!");
        }
    }

    private void createThread() {
        mHandlerThreadMain = new HandlerThread("inference");
        mHandlerThreadMain.start();
        mHandlerMain = new Handler(mHandlerThreadMain.getLooper());
//        mThreadMainStatus = THREAD_CREATED;
    }

    public void onCancelClick(View view) {
        onBackPressed();
    }

    public void onCaptureClick(View view) {
        if(mCroppedBitmap==null) {
            displayToastError(null, "Cannot get face image bitmap");
            return;
        }
        final Bitmap cropped = mCroppedBitmap;
        final Bitmap fullBitmap = mOriginalBitmap;
        setBitmap(fullBitmap);
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_user, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final EditText edtNik = dialogView.findViewById(R.id.edt_nik);
        final EditText edtName = dialogView.findViewById(R.id.edt_name);

        dialog.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nik = edtNik.getText().toString().trim();
                String name = edtName.getText().toString().trim();
                if(!nik.isEmpty() && !name.isEmpty()) {
                    saveUser(nik, name, cropped, fullBitmap);
                } else {
                    displayToastError(null, "NIK dan Nickname tidak boleh kosong");
                }
            }
        });
        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMiniPreview.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    private void saveUser(String nik, String name, Bitmap cropped, Bitmap fullBitmap) {
//        setBitmap(cropped);
        mProgressBar.setVisibility(View.VISIBLE);
        byte[] byteArrayPhoto = Utils.getBitmapAsByteArray(cropped);
        String encodedFile = Utils.getByteArrayAsString64(byteArrayPhoto);

        String ip = Prefs.getString(MainActivity.PREF_DB_IP, MainActivity.DEFAULT_IP);
        String url = MainActivity.URL_HTTP + ip + MainActivity.URL_POST_NEW_USER;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(1000);
        client.setMaxRetriesAndTimeout(2, 1000);
        client.setResponseTimeout(3000);
        RequestParams rparams = new RequestParams();
        rparams.add("user_id", nik);
        rparams.add("name", name);
        rparams.put("thumb", encodedFile);
        if(fullBitmap!=null) {
            Bitmap scaledBitmap = Utils.scaleImageKeepAspectRatio(fullBitmap, 400);
            byte[] byteArrayPhoto2 = Utils.getBitmapAsByteArray(scaledBitmap);
            String encodedFile2 = Utils.getByteArrayAsString64(byteArrayPhoto2);
            rparams.put("image", encodedFile2);
        }
        client.post(url, rparams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                if(statusCode==200) {
                    String message = "Data berhasil disimpan (NIK: " + nik + ", Nama: " + name + ")";
                    displayBottomAttendanceOk(fullBitmap, name, nik, MainActivity.STRING_NEW_PERSON, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String createdAtNow = new DateUtils("-").getCurrentDate();
                String createdOnNow = new DateUtils("-").getCurrentTime();
                long createdDateTimeNow = new DateUtils("-").stringToEpoch(createdAtNow + " " + createdOnNow);
                insertNewUser(nik, name, createdDateTimeNow, encodedFile);
                mProgressBar.setVisibility(View.GONE);
                displayBottomAttendanceOk(fullBitmap, name, nik, MainActivity.STRING_NEW_PERSON, false);
            }
        });
    }

    private void insertNewUser(String nik, String name, long datetime, String face64String) {

        if (!Python.isStarted()) {
            displayToastError(null, "Internal error, cannot start compiler engine");
//            delayedFinishProcessFlag();
            return;
        }
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule("encoder");
        PyObject ret = pythonFile.callAttr("encode", face64String);
        float[] encoding = ret.toJava(float[].class);

        User user = new User(nik, name, datetime, "X");
        mDbHelper.insertUser(user);

        String encArray = "";
        for(int i=0;i<encoding.length; i++) {
            if(i==encoding.length-1) encArray = encArray + Float.toString(encoding[i]);
            else encArray = encArray + Float.toString(encoding[i]) + ",";
        }
        Encoding encodingRecord = new Encoding(nik, encArray);
        mDbHelper.insertEncoding(encodingRecord);
    }

    private void displayBottomAttendanceOk(Bitmap face, String name, String nik, String aType, boolean online) {
        BottomSheetFragmentOk dialog = new BottomSheetFragmentOk();
        dialog.setCancelable(false);
        Bundle bundle = new Bundle();
        bundle.putByteArray(MainActivity.ARGS_BITMAP, Utils.getBitmapAsByteArray(face));
        bundle.putString(MainActivity.ARGS_ATYPE, aType);
        bundle.putString(MainActivity.ARGS_ID, nik);
        bundle.putString(MainActivity.ARGS_NICKNAME, name);
        bundle.putBoolean(MainActivity.ARGS_ONLINE_STATUS, online);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Attendance Ok");
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog!=null) {
                    dialog.dismiss();
                    onBackPressed();
                }
            }
        };
        handler.postDelayed(runnable, 4000);
    }

    private void displayToastSuccess(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DynamicToast.makeSuccess(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayToastError(Throwable throwable, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(throwable!=null && mDebug == true) {
                    DynamicToast.makeError(getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                } else {
                    DynamicToast.makeError(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void processFrame(List<DetectionProto.Detection> detections) {
        mIsProcessing = true;
        final DetectionProto.Detection detection = detections.get(0);
        runInBackground(new Runnable() {
            @Override
            public void run() {
                //
                float previewWidth = (float) mPreviewDisplayView.getWidth();
                float previewHeight = (float) mPreviewDisplayView.getHeight();
                // extract important keypoints
                float[] left_eye_norm = {
                        detection.getLocationData().getRelativeKeypoints(0).getX(),
                        detection.getLocationData().getRelativeKeypoints(0).getY()
                };
                float[] right_eye_norm = {
                        detection.getLocationData().getRelativeKeypoints(1).getX(),
                        detection.getLocationData().getRelativeKeypoints(1).getY()
                };
                float[] left_ear_norm = {
                        detection.getLocationData().getRelativeKeypoints(4).getX(),
                        detection.getLocationData().getRelativeKeypoints(4).getY()
                };
                float[] right_ear_norm = {
                        detection.getLocationData().getRelativeKeypoints(5).getX(),
                        detection.getLocationData().getRelativeKeypoints(5).getY()
                };
                float[] mouth_norm = {
                        detection.getLocationData().getRelativeKeypoints(3).getX(),
                        detection.getLocationData().getRelativeKeypoints(3).getY()
                };
                // de-normalized against screen size & transform translate
                float[] left_eye = {(left_eye_norm[0]*previewWidth), (left_eye_norm[1]*previewHeight)};
                float[] right_eye = {(right_eye_norm[0]*previewWidth), (right_eye_norm[1]*previewHeight)};
                float[] left_ear = {(left_ear_norm[0]*previewWidth), (left_ear_norm[1]*previewHeight)};
                float[] right_ear = {(right_ear_norm[0]*previewWidth), (right_ear_norm[1]*previewHeight)};
                float[] mouth = {(mouth_norm[0]*previewWidth), (mouth_norm[1]*previewHeight)};
                final Bitmap bitmapCopy = Bitmap.createBitmap(
                        (int)previewWidth,
                        (int)previewHeight,
                        Bitmap.Config.ARGB_8888);
                final HandlerThread handlerThread = new HandlerThread("PixelCopier");
                handlerThread.start();
                PixelCopy.request(mPreviewDisplayView, bitmapCopy, new PixelCopy.OnPixelCopyFinishedListener() {
                    @Override
                    public void onPixelCopyFinished(int copyResult) {
                        try {
                            Log.d(TAG,"onPixelCopyFinished");
                            mOriginalBitmap = bitmapCopy;
                            long startTimeForReference = SystemClock.uptimeMillis();

                            // since tensorflow input image is scaled down, so we need to scale it up
                            // to get proper face position
                            Matrix matrixScale = new Matrix();
                            matrixScale.setScale(previewWidth/mTensorImageSize2, 1,
                                    previewWidth/2, previewHeight/2);
                            // re-position the keypoints
                            matrixScale.mapPoints(left_eye);
                            matrixScale.mapPoints(right_eye);
                            matrixScale.mapPoints(left_ear);
                            matrixScale.mapPoints(right_ear);
                            matrixScale.mapPoints(mouth);
                            // since face need to be aligned well before send for inference,
                            // so we need to rotate image based on position of both eyes
                            // get center point of rotation as the center between eyes
                            double rotation = atan2((right_eye[1] - left_eye[1]), (right_eye[0] - left_eye[0]));
                            double degrees = -(toDegrees(rotation));
                            Matrix matrixRotation = new Matrix();
                            matrixRotation.setRotate((float) degrees, previewWidth/2, previewHeight/2);
                            Bitmap rotateBitmap = Bitmap.createBitmap(bitmapCopy, 0, 0,
                                    (int)previewWidth, (int)previewHeight, matrixRotation, true);
                            // width after rotating
                            int w_rotate = rotateBitmap.getWidth();
                            int h_rotate = rotateBitmap.getHeight();
                            // ====>
                            // so after rotating the bitmap, we need to map the right eye
                            // the plane cartesian is now changed, become bigger
                            // first, lets try rotating right eye coordinate around center of original bitmap
                            matrixRotation.mapPoints(right_eye);
                            matrixRotation.mapPoints(left_eye);
                            matrixRotation.mapPoints(right_ear);
                            matrixRotation.mapPoints(left_ear);
                            matrixRotation.mapPoints(mouth);
                            // ====>
                            // then translate the point of right eye
                            Matrix matrix = new Matrix();
                            float dx = ((float) w_rotate - previewWidth) / 2;
                            float dy = ((float) h_rotate - previewHeight) / 2;
                            matrix.setTranslate(dx, dy);
                            matrix.mapPoints(right_eye);
                            matrix.mapPoints(left_eye);
                            matrix.mapPoints(right_ear);
                            matrix.mapPoints(left_ear);
                            matrix.mapPoints(mouth);
                            // ====>
                            // we need to create our own BB after all keypoints are translated to rotated Bitmap
                            // the BB left-right line is relative to left-right eye
                            // get distance between eyes, becasue the eyes now already aligned horizontally
                            // so we dont need to use sqrt to find the distance

                            // define the top-left and bottom of our custom bounding box
                            float deltax = MainActivity.DELTA_X_FACE * abs(right_eye[0] - left_eye[0]);
                            float deltay = MainActivity.DELTA_Y_FACE * abs(right_eye[0] - left_eye[0]);
                            float[] bb_left_top_1 = {left_eye[0] - deltax, left_eye[1] - deltay};
                            float[] bb_right_top_1 = {right_eye[0] + deltax, right_eye[1] - deltax};
                            float[] bb_left_bottom_1 = {left_eye[0] - deltax, mouth[1] + deltax};
                            // re-calculate the size
                            float crop_w = bb_right_top_1[0] - bb_left_top_1[0];
                            float crop_h = bb_left_bottom_1[1] - bb_left_top_1[1];
//                            // scale to 160 x 200 pix so
//                            Matrix matrixScale1 = new Matrix();
//                            matrixScale1.setScale(160 / crop_w, 160 / crop_h, crop_w / 2, crop_h / 2);
                            mCroppedBitmap = Bitmap.createBitmap(rotateBitmap,
                                    (int) bb_left_top_1[0],
                                    (int) bb_left_top_1[1],
                                    (int) crop_w,
                                    (int) crop_h
                            );
                            Log.d(TAG, "Image size of cropped bitmap = " +
                                    mCroppedBitmap.getWidth() + "x" + mCroppedBitmap.getHeight());
//                            setBitmap(mCroppedBitmap);
                            long endTimeForReference = SystemClock.uptimeMillis();
                            String cost = "Inference timecost: " + (endTimeForReference - startTimeForReference);
                            Log.d(TAG, cost);
                        } catch (Exception ex) {
                            // face is not in the center of screen,
                            // half cropped, this will lead to exception,
                            // because coordinate inconsistence
                        } finally {
                            mIsProcessing = false;
                        }
                    }
                }, new Handler(handlerThread.getLooper()));
            }
        });
    }

    private void setBitmap(Bitmap bmp) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                mMiniPreview.setImageBitmap(bmp);
            }
        });
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (mHandlerMain != null) {
            mHandlerMain.post(r);
        }
    }

    private void destroySurface() {
        mProcessor.getVideoSurfaceOutput().setSurface(null);
        mProcessor.close();
        mProcessor = null;
    }

    private void setupPreviewDisplayView() {
        mPreviewDisplayView.setVisibility(View.GONE);
        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
        viewGroup.addView(mPreviewDisplayView);

        mPreviewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                mProcessor.getVideoSurfaceOutput().setSurface(holder.getSurface());
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                // (Re-)Compute the ideal size of the camera-preview display (the area that the
                                // camera-preview frames get rendered onto, potentially with scaling and rotation)
                                // based on the size of the SurfaceView that contains the display.
                                Size viewSize = new Size(width, height);
                                mTensorImageSize = mCameraHelper.computeDisplaySizeFromViewSize(viewSize);

                                // Connect the converter to the camera-preview frames as its input (via
                                // previewFrameTexture), and configure the output width and height as the computed
                                // display size.
                                mConverter.setSurfaceTextureAndAttachToGLContext(
                                        mPreviewFrameTexture, mTensorImageSize.getWidth(), mTensorImageSize.getHeight());
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                holder.removeCallback(this);
                                destroySurface();
                            }
                        });
    }

    private void startCamera() {
        mCameraHelper = new OneCameraXPreviewHelper();
        mCameraHelper.setOnCameraStartedListener(new CameraHelper.OnCameraStartedListener() {
            @Override
            public void onCameraStarted(@javax.annotation.Nullable SurfaceTexture surfaceTexture) {
                mPreviewFrameTexture = surfaceTexture;
                // Make the display view visible to start showing the preview. This triggers the
                // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
                mPreviewDisplayView.setVisibility(View.VISIBLE);
                Log.d(TAG,"Camera Started");
            }
        });
        mCameraHelper.startCamera(this, MainActivity.CAMERA_FACING, /*surfaceTexture=*/ null);
    }

}
