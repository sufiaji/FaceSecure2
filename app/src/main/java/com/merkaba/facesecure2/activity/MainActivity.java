package com.merkaba.facesecure2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.animation.Animator;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.aakira.compoundicontextview.CompoundIconTextView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.DetectionProto;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.glutil.EglManager;
import com.jackandphantom.circularimageview.CircleImage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.merkaba.facesecure2.R;
import com.merkaba.facesecure2.model.Attendance;
import com.merkaba.facesecure2.model.CountDownAnimation;
import com.merkaba.facesecure2.model.Prediction;
import com.merkaba.facesecure2.receiver.EmailAlarmReceiver;
import com.merkaba.facesecure2.utils.DateUtils;
import com.merkaba.facesecure2.model.DebugAdapter;
import com.merkaba.facesecure2.model.DebugLog;
import com.merkaba.facesecure2.model.Encoding;
import com.merkaba.facesecure2.model.OneCameraXPreviewHelper;
import com.merkaba.facesecure2.model.User;
import com.merkaba.facesecure2.utils.DatabaseHelper;
import com.merkaba.facesecure2.utils.SendEmailService;
import com.merkaba.facesecure2.utils.Utils;
//import com.merkaba.facesecure2.view.BottomSheetFragmentConfirm;
import com.merkaba.facesecure2.view.PaintView;
import com.ornach.nobobutton.NoboButton;
import com.pixplicity.easyprefs.library.Prefs;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.vicmikhailau.maskededittext.MaskedEditText;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;
import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import cz.msebera.android.httpclient.Header;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.round;
import static java.lang.Math.toDegrees;

public class MainActivity extends AppCompatActivity { // implements FaceSubscriber.IFaceCallback

    private static final String TAG = "MainActivity";
    public static final String BINARY_GRAPH_NAME = "facedetectioncpu.binarypb";
    public static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    public static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
    public static final String OUTPUT_DETECTIONS_STREAM_NAME = "output_detections";
    public static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
    private static final String PREF_FD_TIMEOUT = "fd_timeout";
    private static final String PREF_FD_DISTANCE = "fd_distance";
    public static final String PREF_DB_IP = "db_ip";
    public static final String PREF_AUTO_MODE = "auto_mode";
    public static final String PREF_AUTO_TIMEOUT_MINUTE = "auto_mode_timeout_minute";
    public static final String PREF_AUTO_TIMEOUT_HOUR = "auto_mode_timeout_hour";
    private static final String PREF_VCOMMAND = "enable_voice_command";
    private static final String PREF_VFEEDBACK = "enable_voice_feedback";
    private static final String PREF_THRESHOLD_FACE_DISTANCE = "threshold_distance";
    private static final String PREF_THRESHOLD_LIVENESS = "threshold_liveness";
    private static final String PREF_TERMINAL_LOCATION = "terminal_location";
    private static final String PREF_SYNC_TIMEOUT = "timeout_sync";
    private static final String PREF_DRAW_BOUNDINGBOX = "draw_bounding_box";
    private static final String PREF_STANDBY_TIMER = "standby_timer";
    private static final String PREF_VOICE_WAITING_TIMER = "voice_timer";
    public static final String PREF_DELAY_BETWEEN_PROCESS = "delay_between_process";
    public static final String PREF_LIVENESS_MANDATORY = "liveness_mandatory";
    public static final String PREF_SMTP_HOST = "smtp_host";
    public static final String PREF_SMTP_PORT = "smtp_port";
    public static final String PREF_SMTP_SENDER_USER = "smtp_sender";
    public static final String PREF_SMTP_SENDER_PASSWORD = "smtp_password";
    public static final String PREF_SMTP_RECEIVER_USER = "smtp_receiver";
    public static final String PREF_SMTP_EMAIL_SEND_TIME = "email_send_time";
    public static final String PREF_SEND_MAIL = "send_email";

    public static final String URL_GET_PING = "/api/v1/facesecure/ping/";
    public static final String URL_GET_PREDICTION = "/api/v1/facesecure/recognize/";
    public static final String URL_POST_NEW_USER = "/api/v1/facesecure/user/";
    public static final String URL_POST_ATTENDANCE = "/api/v1/facesecure/attendance/";
    public static final String URL_POST_UNSENT_ATTENDANCES = "/api/v1/facesecure/attendances/";
    public static final String URL_GET_LAST_ATTENDANCE = "/api/v1/facesecure/attendance/";
    public static final String URL_GET_ALL_USER = "/api/v1/facesecure/user/all/";
    public static final String URL_GET_ALL_ENCODING = "/api/v1/facesecure/encoding/all/";
    public static final String URL_GET_LIVENESS = "/api/v1/facesecure/liveness/";
    public static final String URL_HTTP = "http://";
    public static final String SERVER_PORT = "5000";
    public static final String STRING_CLOCK_IN = "P10";
    public static final String STRING_CLOCK_OUT = "P20";
    public static final String STRING_NEW_PERSON = "new_person";
    public static final String STRING_CLOCK_CANCEL = "cancel";
    public static final String FACE_STATUS = "face_status";
    public static final String FACE_REAL = "face_real";
    public static final String FACE_FAKE = "face_fake";
    public static final String FACE_UNAVAIL = "face_liveness_unavailable";
    public static final String EMAIl_SUBJECT = "email_subject";
    public static final String EMAIl_ATTACHMENT = "email_attachment";

    public static final String ARGS_ID = "nik";
    public static final String ARGS_BITMAP = "bitmap";
    public static final String ARGS_TIMER_WAITING_VOICE_COMMAND = "timer_waiting_voice_command";
    public static final String ARGS_ONLINE_STATUS = "online_status";
    public static final String ARGS_NICKNAME = "nickname";
    public static final String ARGS_ATYPE = "attendance_type";
    public static final String ARGS_MESSAGE_TYPE = "message_type";
    public static final String ARGS_MESSAGE_CONTENT = "message_content";
    public static final String MESSAGE_SUCCESS = "success";
    public static final String MESSAGE_ERROR = "error";
    public static final String MESSAGE_WARNING = "warning";
    public static final String MESSAGE_INFO = "info";
    public static final String MESSAGE_PERSON_UNKNOWN = "person_unknown";

    public static final String VOICE_COMMAND_LANG = "id";

    public static final String NA = "na";
    public static final String DEFAULT_IP = "0.0.0.0:0000";
//    private static final String THREAD_DESTROYED = "destroyed";
//    private static final String THREAD_CREATED = "created";
//    private static final String THREAD_RUN = "running";
    public static final int ENCODING_LENGTH = 128;
    public static final float DELTA_X_FACE = 0.4f;
    public static final float DELTA_Y_FACE = 0.6f;
    public static final String CARRIAGE_RETURN = System.getProperty("line.separator");

//    private final int mSpeakCounterMax = 5;

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
    private boolean mIsProcessing = false;

    // thread
    private HandlerThread mHandlerThreadMain;
    private Handler mHandlerMain;

    // face recognition inference image size
    private boolean mDebug = false;
    private boolean mPostAttendanceDuringDebug = false;
    private boolean mNoPostAttendance = false;
    private SpinKitView mProgressSpinKit;
    private ProgressBar mProgressBarGreen;
    private ImageView imView;
    private int mFdTimeout = 1; // seconds
    private int mSyncTimeout = 30; // minutes
    private int mStandbyTimeout = 30; // seconds

    private Runnable mFDRunnableTimer;
    private Runnable mSyncRunnableTimer;
    private Runnable mStandbyRunnableTimer;
    private Runnable mLivenessRunnableThread;
    private ScheduledThreadPoolExecutor mSchedulerExecutor;
    private ScheduledFuture<?> mFDTimer;
    private ScheduledFuture<?> mSyncTimer;
    private ScheduledFuture<?> mStandbyTimer;
    private Handler mHandlerTimer;
    private static final int TIMER_OVERFLOW_FD = 1;
    private static final int TIMER_OVERFLOW_SYNC = 2;
    private static final int TIMER_OVERFLOW_STANDBY = 3;
    private static final int THREAD_LIVENESS = 4;
    private static final int FACE_NEAR = 0;
    private static final int FACE_MEDIUM = 1;
    private static final int FACE_FAR = 2;

    private boolean mIsStart = false;
    private boolean mPreviousStartStatus = false;
    private boolean mAutoMode = true;
    private boolean mVoiceFeedback = true;
    private boolean mVoiceCommand = true;
    private boolean mFDTimerIsRun = false;
    private boolean mStandbyTimerIsRun = false;
    private String mFaceLivenessStatus = "NA"; // fake, real/na

    private SpinKitView mIndicator;
    private List<DetectionProto.Detection> mDetectionVector;
    private Bitmap mCroppedBitmap = null;
    private int mTensorImageSize = 640;
    private String mIpAndPort;
    private int mAutoTimeoutMinutes = 59; // 60 minutes
    private int mAutoTimeoutHours = 0; // 0 hour
    private int mVoiceCommandWaitingTimer = 10;

    private Context mContext = this;
    private ArrayList<DebugLog> mDebugLog;
    private DebugAdapter mDebugAdapter;
    private ListView mListView;
    private AppCompatTextView tvProgress;
    private Button mClearListDebugButton;
    private FloatingActionButton fabStart;

    private int mDelayTimeBetweenProcess = 5; //5 detik
    private float mThresholdDistanceFaceEmbedding = 0.5f;
    private float mThresholdLiveness = 0.1f; // smaller more straight, real face has small value like 0.01, fake has big value like 0.70
    public static int mLivenessCounterMax = 10; // take 10 frames to decide fake/real
    public float THRESHOLD_REAL_NUM_FRAME_PERCENTAGE = 0.8f; // if 80% of total frame within time range are real, then it is REAL

    private DatabaseHelper mDbHelper;
    private LinearLayout mLayoutBlack;
    private boolean mFlagDrawBoundingBox;
    private int mFaceDistanceCategory = 1; //0:near, 1:medium, 2:far
    private int mLivenessFrameCounter = 0;
    private int mLivenessRealCounter = 0;
    private int mLivenessFakeCounter = 0;
    private int mLivenessUnavailCounter = 0;
    private float[] mFaceAreas = new float[] {0.4f, 0.2f, 0.1f}; // 60, 45, 30cm

    private String mSmtpHost = "smtp.email.com";
    private String mSmtpPort = "465";
    private String mSmtpSender = "sender@email.com";
    private String mSmtpPassword = "password";
    private String mSmtpReceiver = "receiver@email.com";
    private String mSmtpEmailTime = "23:45";
    private boolean mSendMail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showLog("onCreate");
        super.onCreate(savedInstanceState);
        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //
        hideNavbar();
        //
        setContentView(R.layout.activity_main6);
        mLayoutBlack = findViewById(R.id.layout_black);
        mLayoutBlack.setVisibility(View.GONE);
        mPaintView = findViewById(R.id.paintView);
        imView = findViewById(R.id.imview);
        //
        createNotificationChannel();
        //
        setupDebugList();
        //
        mProgressSpinKit = findViewById(R.id.spinkit_progress_circle);
        mProgressBarGreen = findViewById(R.id.progress_center);
        mProgressBarGreen.setVisibility(View.VISIBLE);
        tvProgress = findViewById(R.id.textProgress);
        hideTextProgress();
        mIndicator = findViewById(R.id.indicator);

        AndroidAssetUtil.initializeNativeAssetManager(this);
        // get data from preference so that we dont need to get it everytime
        mFdTimeout = Prefs.getInt(PREF_FD_TIMEOUT, 3);
        mIpAndPort = Prefs.getString(MainActivity.PREF_DB_IP, DEFAULT_IP);
        // default is AutoMode with 60 minutes timeout
        mAutoMode = Prefs.getBoolean(PREF_AUTO_MODE, true);
        mAutoTimeoutMinutes = Prefs.getInt(PREF_AUTO_TIMEOUT_MINUTE, 59); // minutes
        mAutoTimeoutHours = Prefs.getInt(PREF_AUTO_TIMEOUT_HOUR, 0);
        mVoiceCommand = Prefs.getBoolean(PREF_VCOMMAND, true);
        mVoiceFeedback = Prefs.getBoolean(PREF_VFEEDBACK, true);
        mThresholdDistanceFaceEmbedding = Prefs.getFloat(PREF_THRESHOLD_FACE_DISTANCE, 0.5f);
        mThresholdLiveness = Prefs.getFloat(PREF_THRESHOLD_LIVENESS, 0.1f);
        mLocation = Prefs.getString(PREF_TERMINAL_LOCATION, "00");
        mSyncTimeout = Prefs.getInt(PREF_SYNC_TIMEOUT, 30);
        mFlagDrawBoundingBox = Prefs.getBoolean(PREF_DRAW_BOUNDINGBOX, true);
        mFaceDistanceCategory = Prefs.getInt(PREF_FD_DISTANCE, FACE_MEDIUM);
        //
        mStandbyTimeout = Prefs.getInt(PREF_STANDBY_TIMER, 30);
        mVoiceCommandWaitingTimer = Prefs.getInt(PREF_VOICE_WAITING_TIMER, 10);
        //
        mDelayTimeBetweenProcess = Prefs.getInt(PREF_DELAY_BETWEEN_PROCESS, 5);
        mLivenessIsMandatory = Prefs.getBoolean(PREF_LIVENESS_MANDATORY, true);
        //
        mSmtpHost = Prefs.getString(PREF_SMTP_HOST, mSmtpHost);
        mSmtpPort = Prefs.getString(PREF_SMTP_PORT, mSmtpPort);
        mSmtpSender = Prefs.getString(PREF_SMTP_SENDER_USER, mSmtpSender);
        mSmtpPassword = Prefs.getString(PREF_SMTP_SENDER_PASSWORD, mSmtpPassword);
        mSmtpReceiver = Prefs.getString(PREF_SMTP_RECEIVER_USER, mSmtpReceiver);
        mSmtpEmailTime = Prefs.getString(PREF_SMTP_EMAIL_SEND_TIME, mSmtpEmailTime);
        mSendMail = Prefs.getBoolean(PREF_SEND_MAIL, mSendMail);
        //
        assignFabListener();
        //
        PermissionHelper.checkAndRequestCameraPermissions(this);
        PermissionHelper.checkAndRequestAudioPermissions(this);
        // prepare timer
        prepareAllThreads();
        //
        resetSpeechRecognizer();
        setRecognizerIntent();
        //
        prepareTTS();
        //
        mDbHelper = new DatabaseHelper(this);
        //
        mSupport64bit = true;
        Python python = Python.getInstance();
        try {
            mPyObjectLiveness = python.getModule("liveness").callAttr("LivenessDetection");
        } catch (Exception ex) {
            displayToastError(null, "Unsupported device. Need to have 64bit OS and Processor");
            mSupport64bit = false;
            mLivenessCounterMax = 5;
        }
        //
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        //
        broadcastEmailAlarm();
        //
        initScanAnim();

        // init blur code
        prepareBlurBackground();

    }

    private BlurView mBlurView;
    private ImageView mImageViewBlur;
    private void prepareBlurBackground() {
        float radius = 12f;
        mBlurView = findViewById(R.id.layoutBlur);
        mImageViewBlur = findViewById(R.id.image_blur);

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        mBlurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
        mBlurView.setVisibility(View.INVISIBLE);
        mImageViewBlur.setVisibility(View.INVISIBLE);
    }

    private int mVolume;
    private AudioManager mAudioManager;

    private boolean mSupport64bit = true;
    PyObject mPyObjectLiveness;
    FloatingActionMenu mFabMenu;

    @Override
    protected void onResume() {
        showDebug("onResume");
        showLog("onResume");

        super.onResume();
        //
        createThread();
        //
        mIsStart = mPreviousStartStatus;
        mPreviewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();
        mEglManager = new EglManager(null);
        mProcessor =
                new FrameProcessor(
                        this,
                        mEglManager.getNativeContext(),
                        BINARY_GRAPH_NAME,
                        INPUT_VIDEO_STREAM_NAME,
                        OUTPUT_VIDEO_STREAM_NAME);
        mProcessor.getVideoSurfaceOutput().setFlipY(FLIP_FRAMES_VERTICALLY);
        mProcessor.addPacketCallback(
                OUTPUT_DETECTIONS_STREAM_NAME,
                (packet) -> {
                    if(mIsStart) {
                        // for blurring background
                        copyScreen();
                        drawBitmapBlur();
                        //
                        List<DetectionProto.Detection> detections = PacketGetter.getProtoVector(packet, DetectionProto.Detection.parser());
                        if (detections.size() > 0) {
                            /**
                             * Get detection score, filter only if score is equal or more than 99%
                             */
                            DetectionProto.Detection det = detections.get(0);
                            List<Float> listOfScore = det.getScoreList();
                            float confEx = 0;
                            if(listOfScore.size()>0) {
                                confEx = listOfScore.get(0);
//                                Log.d(TAG,"confidence face: " + confEx);
                                if(confEx>=0.97f) {
                                    // we have a valid face
                                    mDetectionVector = detections;
                                    /**
                                     * drawBoundingBox ==>
                                     * we draw Bounding Box out of any process and evaluate standby timer
                                     * Bounding box will always be available anytime when program run
                                     * standby timer de-activated if BB is green
                                     */
                                    evaluateFaceOnThread(detections.get(0));
                                    if(!mIsProcessing) {
                                        // we evaluate face first
                                        // if face area is large enough then trigger the timer and stop standby screen
                                        // startFDTimer();
                                    }
                                } else {
                                    clearBB();
                                }
                            } else {
                                clearBB();
                            }
                        } else {
                            if(mDetectionVector!=null) mDetectionVector.clear();
                            stopFDTimer();
                            clearBB();
//                            startAnim = false;
                            hideScanAnim();
                            hideTextProgress();
//                            hideScanFrame();
                            resetLivenessVariables();
                            startStandbyTimer();
                        }
                    } else {
                        clearBB();
                    }

                });
        mConverter = new ExternalTextureConverter(mEglManager.getContext());
        mConverter.setFlipY(FLIP_FRAMES_VERTICALLY);
        mConverter.setConsumer(mProcessor);
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
        if(mPreviousStartStatus) startProgram();
        hideProgressSpinKit();
        resetSpeechRecognizer();
        mProgressBarGreen.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        showDebug("onPause");
        showLog("onPause");
        destroyThread();
        super.onPause();
        mConverter.close();
        mConverter = null;
        mCameraHelper.unBind();
        mCameraHelper = null;
        mPreviewDisplayView = null;
        stopFDTimer();
        hideScanAnim();
        stopSyncTimer();
        stopStandbyTimer();
        mPreviousStartStatus = mIsStart;
        if(mIsStart) stopProgram();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        showDebug("onDestroy");
        super.onDestroy();
        if(mTextToSpeech!=null)
            mTextToSpeech.shutdown();
        if(mSchedulerExecutor!=null)
            mSchedulerExecutor.shutdown();
        mDbHelper.closeDB();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showDebug("onWindowFocusChanged");
        if(hasFocus)
            hideNavbar();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    private void mute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
            }
        });

    }

    private void unMute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mVolume, 0);
            }
        });
    }

    private void clearBB() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPaintView.clear();
            }
        });
    }

    private ImageView mImageScan;
    private void initScanAnim() {
        LottieDrawable lottieDrawable = new LottieDrawable();

        mImageScan = findViewById(R.id.image_anim_scan);
        mImageScan.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mImageScan.setImageDrawable(lottieDrawable);
        LottieComposition.Factory.fromAssetFileName(this, "scan4.json", (composition) -> {
            lottieDrawable.setComposition(composition);
            lottieDrawable.loop(true);

            lottieDrawable.playAnimation();
        });
        mImageScan.setVisibility(View.INVISIBLE);
    }

    private boolean isShowScanner = false;
    private boolean startAnim = false;
    private void showScanAnim(int Y) {
        if(!isShowScanner) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageScan.setVisibility(View.VISIBLE);
                    mImageScan.requestLayout();
                    mImageScan.setY(Y);
                    isShowScanner = true;
                }
            });

        }
    }

    private void hideScanAnim() {
        startAnim = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageScan.setVisibility(View.INVISIBLE);
                isShowScanner = false;
            }
        });
    }

    private void setupDebugList() {

        mDebugLog = new ArrayList<>();
        mDebugLog.add(new DebugLog("Begin:"));
        mDebugAdapter = new DebugAdapter(mDebugLog, mContext);
        mListView = findViewById(R.id.list);
        mClearListDebugButton = findViewById(R.id.btn_clear_list);
        mListView.setAdapter(mDebugAdapter);
    }

    private void showDebugListView() {
        mListView.setVisibility(View.VISIBLE);
        imView.setVisibility(View.VISIBLE);
        mClearListDebugButton.setVisibility(View.VISIBLE);
    }

    private void hideDebugListView() {
        mListView.setVisibility(View.GONE);
        imView.setVisibility(View.GONE);
        mClearListDebugButton.setVisibility(View.GONE);
    }

    private void destroyThread() {
        showDebug("Thread destroyed");
        mHandlerThreadMain.quitSafely();
        try {
            mHandlerThreadMain.join();
            mHandlerThreadMain = null;
            mHandlerMain = null;
        } catch (final InterruptedException e) {
            showDebug("InterruptedException!");
        }
    }

    private void createThread() {
        mHandlerThreadMain = new HandlerThread("inference");
        mHandlerThreadMain.start();
        mHandlerMain = new Handler(mHandlerThreadMain.getLooper());
    }

    private synchronized void beginProcessAttendance() {
        List<DetectionProto.Detection> ds = mDetectionVector;
        if(ds.size()>0) {
            showDebug("beginProcessAttendance");
            DetectionProto.Detection d = ds.get(0);
            doPingAndPost(d);
        }

    }

    private boolean mTimerFDAndLivenessIsFinished = true;
    private void prepareAllThreads() {
        mSchedulerExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(6);
        mHandlerTimer = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case TIMER_OVERFLOW_FD:
                        mFDTimerIsRun = false;
                        if(mFaceLivenessStatus.equalsIgnoreCase("NA")) {
//                            showDebug("Liveness Process is not finished yet");
                        } else {
//                            showDebug("Liveness status on Timer: " + mFaceLivenessStatus);
                            mIsProcessing = true;
//                            startAnim = true;
                            if(mFaceLivenessStatus.equalsIgnoreCase(FACE_REAL)) {
                                // if Liveness already finished and status is REAL, continue process attendnace
                                showDebug("Liveness status on Timer REAL, begin process Attendance");
                                startAnim = true;
                                beginProcessAttendance();

                            } else if(mFaceLivenessStatus.equalsIgnoreCase(FACE_FAKE)) {
//                                mIsProcessing = true;
                                displayToastError(null, "Gambar terdeteksi");
                                delayedFinishProcessFlag();
                            } else if(mFaceLivenessStatus.equalsIgnoreCase(FACE_UNAVAIL)) {
//                                mIsProcessing = true;
                                displayToastError(null, "Liveness Detection tidak tersedia, proses dibatalkan.");
                                delayedFinishProcessFlag();
                            }
                            mTimerFDAndLivenessIsFinished = true;
                        }

                        break;
                    case TIMER_OVERFLOW_SYNC:
                        showDebug("TIMER SYNC Overflow");
                        doSync();
                        break;
                    case TIMER_OVERFLOW_STANDBY:
                        showDebug("TIMER STANDBY Overflow");
                        Log.d(TAG, "STANDBY Timer overflow");
                        screenOff();
                        mStandbyTimerIsRun = false;
                        break;
                    case THREAD_LIVENESS:
                        mLivenessFrameCounter++;
                        Bundle bundle = msg.getData();
                        String faceStatus = bundle.getString(FACE_STATUS);
                        if(faceStatus.equalsIgnoreCase(FACE_REAL)) {
                            mLivenessRealCounter++;
                        } else if(faceStatus.equalsIgnoreCase(FACE_FAKE)) {
                            mLivenessFakeCounter++;
                        } else if(faceStatus.equalsIgnoreCase(FACE_UNAVAIL)) {
                            mLivenessUnavailCounter++;
                        }
                        if(mLivenessFrameCounter >= mLivenessCounterMax -1) {
                            // check if Timer FD overflow already
                            mRequestLiveness = false;
//                            showDebug(">>>> Liveness counter finished. <<<<");
                            if(!mFDTimerIsRun) {
                                /**
                                 * BOTH FINISHED
                                 */
//                                showDebug("Timer finish on liveness");
                                mIsProcessing = true;
                                mTimerFDAndLivenessIsFinished = true;
//                                startAnim = true;
                                // check if face is real
                                float realPortion = (float) mLivenessRealCounter / (float) mLivenessFrameCounter;
                                if(realPortion >= THRESHOLD_REAL_NUM_FRAME_PERCENTAGE) {
                                    // ok, this is REAL face & Timer FD finished
                                    // so just start attendance process
                                    showDebug("Liveness status REAL, begin process attendance");
                                    if(mDebug)
                                        displayToastSuccess("REAL");
                                    startAnim = true;
                                    beginProcessAttendance();

                                } else {
//                                    mIsProcessing = true;
                                    if(mLivenessUnavailCounter > 0)
                                        displayToastError(null, "Liveness Detection tidak tersedia, proses dibatalkan.");
                                    else
                                        displayToastError(null, "Gambar terdeteksi");
                                    delayedFinishProcessFlag();
                                }
                            } else {
                                // Timer FD not yet finish
//                                showDebug("Timer not yet finished");
                                // broadcast status, let FD Timer do the process attendance
                                float realPortion = (float) mLivenessRealCounter / (float) mLivenessFrameCounter;
                                if(realPortion >= THRESHOLD_REAL_NUM_FRAME_PERCENTAGE) {
                                    // ok, this is REAL face
//                                    showDebug("Broadcast Real");
                                    mFaceLivenessStatus = FACE_REAL;
                                    if(mDebug)
                                        displayToastSuccess("REAL");
                                } else {
                                    if(mLivenessUnavailCounter > 0)
                                        mFaceLivenessStatus = FACE_UNAVAIL;
                                    else
                                        mFaceLivenessStatus = FACE_FAKE;
                                }
                            }
                            resetLivenessVariables();
                        }
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        mFDRunnableTimer = new Runnable() {
            @Override
            public void run() {
                Message msg = mHandlerTimer.obtainMessage();
                msg.what = TIMER_OVERFLOW_FD;
                Bundle b = new Bundle();
                b.putInt("fd_overflow", 1);
                msg.setData(b);
                mHandlerTimer.sendMessage(msg);
            }
        };

        mSyncRunnableTimer = new Runnable() {
            @Override
            public void run() {
                Message msg = mHandlerTimer.obtainMessage();
                msg.what = TIMER_OVERFLOW_SYNC;
                Bundle b = new Bundle();
                b.putInt("sync_overflow", 1);
                msg.setData(b);
                mHandlerTimer.sendMessage(msg);
            }
        };

        mStandbyRunnableTimer = new Runnable() {
            @Override
            public void run() {
                Message msg = mHandlerTimer.obtainMessage();
                msg.what = TIMER_OVERFLOW_STANDBY;
                Bundle b = new Bundle();
                b.putInt("standby_overflow", 1);
                msg.setData(b);
                mHandlerTimer.sendMessage(msg);
            }
        };

        mLivenessRunnableThread = new Runnable() {
            @Override
            public void run() {
                if(mDetectionVector.size()>0)
                    livenessDetection(mDetectionVector.get(0));
            }
        };

        startSyncTimer();
    }

    private void startFDTimer() {
        // start timer only if not already been started
        if(!mFDTimerIsRun) {
//            showDebug("FD TIMER started (" + mFdTimeout + " seconds overflow)");
            mFDTimer = mSchedulerExecutor.schedule(mFDRunnableTimer, mFdTimeout, TimeUnit.SECONDS);
            mFDTimerIsRun = true;
        }
    }

    private void stopFDTimer() {
        if(mFDTimer !=null) {
            if(mFDTimerIsRun) {
                mFDTimer.cancel(true);
                mFDTimerIsRun = false;
                hideTextProgress();
                hideProgressSpinKit();
            }
        }
    }

    private void startLivenessThread() {
//        showDebug("Start Liveness Thread");
        mSchedulerExecutor.execute(mLivenessRunnableThread);
    }

    private void startSyncTimer() {
        if(mSyncTimer!=null) mSyncTimer.cancel(false);
        showDebug("SYNC TIMER started (" + mSyncTimeout + " seconds overflow)");
        mSyncTimer = mSchedulerExecutor.scheduleAtFixedRate(mSyncRunnableTimer, mSyncTimeout, mSyncTimeout, TimeUnit.MINUTES);
    }

    private void stopSyncTimer() {
        if(mSyncTimer!=null) mSyncTimer.cancel(false);
    }

    private void startStandbyTimer() {
        if(mStandbyTimer==null) {
            mStandbyTimer = mSchedulerExecutor.schedule(mStandbyRunnableTimer, mStandbyTimeout, TimeUnit.SECONDS);
            Log.d(TAG, "START Standby Timer");
            mStandbyTimerIsRun = true;
        } else {
            if(!mStandbyTimerIsRun) {
                showDebug("STANDBY Timer started (" + mStandbyTimeout + " seconds overflow)");
                mStandbyTimer = mSchedulerExecutor.schedule(mStandbyRunnableTimer, mStandbyTimeout, TimeUnit.SECONDS);
                Log.d(TAG, "START Standby Timer");
                mStandbyTimerIsRun = true;
            }
        }
    }

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    private void setRecognizerIntent() {
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                VOICE_COMMAND_LANG);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
    }

    private void resetSpeechRecognizerUIThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetSpeechRecognizer();
            }
        });
    }

    private String mWord = "";
    private void resetSpeechRecognizer() {
        Log.d(TAG, "Reset Speech Recoginzer");
        if(mSpeechRecognizer != null)
            mSpeechRecognizer.destroy();
        mWord = "";
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Log.d(TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        if(SpeechRecognizer.isRecognitionAvailable(this))
            mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    Log.d(TAG, "onReadyForSpeech");
                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d(TAG, "onBeginningOfSpeech");
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    Log.d(TAG, "onRmsChange");
//                    Log.d()
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    Log.d(TAG, "onBufferReceived: " + buffer);
                }

                @Override
                public void onEndOfSpeech() {
                    Log.d(TAG, "onEndOfSpeech" );
                    showDebug("onEndSpeech:");
                    showDebug("CAPTURED WORDS: " + mWord);
                }

                @Override
                public void onError(int error) {
                    mute();
                    Log.d(TAG, "onError" + Integer.toString(error));
                    resetSpeechRecognizer();
                    startListeningVoiceCommandUIThreadNoSound();
                }

                @Override
                public void onResults(Bundle results) {
                    Log.d(TAG, "onResult");
                    showDebug("SpeechRecognizer: onResults event triggered");
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if(matches != null) {
                        String allMatches = "";
                        for(String word:matches) {
                            allMatches = word + ", ";
                        }
                        mWord = allMatches;
                        showDebug("CAPTURED WORDS: "+allMatches);
                        Log.d(TAG, "results: " + allMatches);
                    }
                    //displaying the first match
                    if (matches != null) {
                        for(String word : matches) {
                            if(word.contains("clock in")
                                    || word.contains("going in")
                                    || word.contains("login")
                                    || word.contains("masuk")
                                    || word.contains("bakso")
                                    || word.contains("in")) {
                                onBottomSheetButtonClick(mUserIdPostAttendance, mUsernamePostAttendance, STRING_CLOCK_IN, mIsOnline, /*mCroppedBitmap*/ mBitmapBig);
                                break;
                            } else if(word.contains("clock out") ||
                                    word.contains("logout") ||
                                    word.contains("luar") ||
                                    word.contains("going out") ||
                                    word.contains("keluar") ||
                                    word.contains("out") ||
                                    word.contains("om") ||
                                    word.contains("south") ||
                                    word.contains("oud") ||
                                    word.contains("ot") ||
                                    word.contains("aut") ||
                                    word.contains("both")) {
                                onBottomSheetButtonClick(mUserIdPostAttendance, mUsernamePostAttendance, STRING_CLOCK_OUT, mIsOnline, /*mCroppedBitmap*/ mBitmapBig);
                                break;
                            } else if(word.contains("batal") ||
                                        word.contains("cancel")) {
//                                onBottomSheetButtonClick(mUserIdPostAttendance, mUsernamePostAttendance, STRING_CLOCK_CANCEL, mIsOnline, mCroppedBitmap);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if(data==null) return;
                    String word = (String) data.get(data.size() - 1);
                    showDebug("partial_results: " + word);
//                    recognisedText.setText(word);
                    Log.d(TAG, "partial_results: " + word);
                    //
                    if(word.contains("clock in")
                            || word.contains("going in")
                            || word.contains("login")
                            || word.contains("masuk")
                            || word.contains("in")) {
                        onBottomSheetButtonClick(mUserIdPostAttendance, mUsernamePostAttendance, STRING_CLOCK_IN, mIsOnline, /*mCroppedBitmap*/ mBitmapBig);

                    } else if(word.contains("clock out") ||
                            word.contains("logout") ||
                            word.contains("luar") ||
                            word.contains("going out") ||
                            word.contains("keluar") ||
                            word.contains("out") ||
                            word.contains("om") ||
                            word.contains("south") ||
                            word.contains("oud") ||
                            word.contains("ot") ||
                            word.contains("aut") ||
                            word.contains("both")) {
                        onBottomSheetButtonClick(mUserIdPostAttendance, mUsernamePostAttendance, STRING_CLOCK_OUT, mIsOnline, /*mCroppedBitmap*/ mBitmapBig);

                    } else if(word.contains("batal") ||
                            word.contains("cancel")) {
//                                onBottomSheetButtonClick(mUserIdPostAttendance, mUsernamePostAttendance, STRING_CLOCK_CANCEL, mIsOnline, mCroppedBitmap);

                    }
                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });

    }

    private void stopStandbyTimerForProcess() {
        stopStandbyTimer();
        screenOn();
    }

    private void stopStandbyTimer() {
        if(mStandbyTimer!=null && mStandbyTimerIsRun) {
            mStandbyTimer.cancel(true);
            mStandbyTimerIsRun = false;
            showDebug("STANDBY Timer stopped");
            Log.d(TAG, "STOP Standby Timer");
        }
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private TextToSpeech mTextToSpeech;
    private void prepareTTS() {
        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = mTextToSpeech.setLanguage(Locale.getDefault());
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        showLog("The Language is not supported!");
                    } else {
                        showLog("Language Supported.");
                    }
                    showLog("Initialization success.");
                } else {
                    displayBottomMessageError(null, "Text-to-Speech is unavailable");
                }
            }
        });
        if(mTextToSpeech!=null) {
            mTextToSpeech.setSpeechRate(1.8f);
        }
    }

    private void speakFeedback(String msg) {
        if(mVoiceFeedback) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int speechStatus = mTextToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
                    if (speechStatus == TextToSpeech.ERROR) {
                        showDebug("Error in converting Text to Speech!");
                        showLog("Error in converting Text to Speech!");
                    }
                }
            });
        }
    }

    private void showTextProgress(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvProgress.setText(text);
            }
        });
    }

    private void hideTextProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvProgress.setVisibility(View.GONE);
            }
        });
    }

    private void stopProgram() {
        stopFDTimer();
        fabStart.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        mIndicator.setVisibility(View.GONE);
        mIsStart = false;
        hideTextProgress();
        hideProgressSpinKit();
        clearBB();
        hideScanAnim();
        resetLivenessVariables();
        showDebug("FD TIMER stopped");
        stopStandbyTimer();
    }

    private void startProgram() {
        fabStart.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        mIndicator.setVisibility(View.VISIBLE);
        mIsStart = true;
        showDebug("Program started");
    }

    private void onStartClick() {

        if(mIsStart)
            stopProgram();
        else
            startProgram();
    }

    private void testdisplay() {
        Bitmap face = BitmapFactory.decodeResource(getResources(), R.drawable.aji_thumb1);
        displayBottomAttendanceOk(face, "Pradhono Aji", "123456789", STRING_CLOCK_OUT, true );
//        displayBottomMessageUnknown();
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.aji_thumb1);
//        displayBottomAttendanceConfirm("12345678", "Pradhono Rakhmono", face, false);
    }

    private void assignFabListener() {
        mFabMenu = findViewById(R.id.fab_menu);
        mFabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!mFabMenu.isOpened())
//                    showBlurBackground();
            }
        });
        //
        fabStart = findViewById(R.id.fab_start);
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(false);
//                hideBlurBackground();
                onStartClick();
//                testdisplay();
            }
        });
        //
        FloatingActionButton fabAddUser = findViewById(R.id.fab_add_user);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 10/05/2020 set disable when click and enable when finished
                mFabMenu.close(false);
//                hideBlurBackground();
                onAddUser();
            }
        });
        //
        FloatingActionButton fabFace = findViewById(R.id.fab_face);
        fabFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 10/05/2020 set disable when click and enable when finished
                mFabMenu.close(false);
                onSettingFaceDetection();
            }
        });

        FloatingActionButton fabSpeech = findViewById(R.id.fab_speech);
        fabSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 10/05/2020 set disable when click and enable when finished
                mFabMenu.close(false);
                onVoiceClick();
            }
        });

        FloatingActionButton fabDebug = findViewById(R.id.fab_debug);
        fabDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptPasswordDebug();
            }
        });
        FloatingActionButton fabLocation = findViewById(R.id.fab_location);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLocationClick();
            }
        });
        FloatingActionButton fabSync = findViewById(R.id.fab_sync);
        fabSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(false);
                onSettingSyncClick();
            }
        });
        //
        FloatingActionButton fabMail = findViewById(R.id.fab_mail);
        fabMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(false);
                onEmailClick();
            }
        });

    }

    private void syncNow() {
        new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.colorPrimary)
                .setNegativeButtonColorRes(R.color.colorRed)
                .setPositiveButtonColorRes(R.color.colorPrimaryDark)
                .setIcon(R.drawable.ic_sync)
                // TODO: language
//                .setTitle("Sync Confirmation")
//                .setMessage("Sync will upload attendance data to Server, and will download user data from Server. It may takes sometimes. Continue?")
                .setTitle("Konfirmasi sinkronisasi")
                .setMessage("Sinkronisasi akan mengunggah data Absensi dan mengunduh data User. Hal ini akan membutuhkan beberapa waktu...")
                .setCancelable(false)
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doPingAndSync();
                    }
                })
                .setNegativeButton("Batal", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }

    public void onLayoutBlackClick(View view) {
        stopStandbyTimer();
        mLayoutBlack.setVisibility(View.GONE);
    }

    private void doSync() {
        // send unsent attendance data to server, if any...
        uploadAttendance();
        // get user data and encoding data
        downloadUser();
        downloadEncoding();
    }

    private void doManualSync() {
        // start progressbar
        displayProgressGreen();
        // disable FabMenu
        disableMenu();
        //
        doSync();
    }

    private boolean mDownloadUser = true;
    private void downloadUser() {
        mDownloadUser = false;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(3, 1000);
        String url = URL_HTTP + mIpAndPort + URL_GET_ALL_USER;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                if(str.isEmpty()) {
                    mDownloadUser = true;
                    checkSyncAndStopProgress(true);
                    return;
                }
                try {
                    JSONObject jsonResponse = new JSONObject(str);
                    // get 1st json for the length of data
                    JSONObject jsonObject = jsonResponse.getJSONObject("0");
                    int len = Integer.parseInt(jsonObject.getString("length"));
                    for(int i=0;i<len;i++) {
                        JSONObject jo = jsonResponse.getJSONObject(Integer.toString(i+1));
                        String userId = jo.getString("user_id");
                        String name = jo.getString("name");
                        String createdAt = jo.getString("created_at");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Date dt = sdf.parse(createdAt);
                        int epoch = (int) dt.getTime();
                        // only insert the Delta
                        if(mDbHelper.userExists(userId)) {
                            // reset local flag
                            mDbHelper.setUserLocalFlag(userId, " ");
                            continue;
                        }
                        User user = new User(userId, name, epoch, " ");
                        long s = mDbHelper.insertUser(user);
                    }
                    // delete local user which is left intact...
                    mDbHelper.deleteLocalUserData();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                mDownloadUser = true;
                checkSyncAndStopProgress(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mDownloadUser = true;
                checkSyncAndStopProgress(false);
            }
        });
    }

    private boolean mDownloadEncoding = true;
    private void downloadEncoding() {
        mDownloadEncoding = false;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(3, 1000);
        String url = URL_HTTP + mIpAndPort + URL_GET_ALL_ENCODING;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    JSONObject jsonObjectLen = jsonObject.getJSONObject("0");
                    int len = Integer.parseInt(jsonObjectLen.getString("length"));
                    for(int i=0;i<len;i++) {
                        JSONObject jo = jsonObject.getJSONObject(Integer.toString(i+1));
                        String userId = jo.getString("user_id");
                        String encArray = jo.getString("encodings");
                        if(mDbHelper.encodingExists(userId)) continue;
                        Encoding encoding = new Encoding(userId, encArray);
                        mDbHelper.insertEncoding(encoding);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mDownloadEncoding = true;
                checkSyncAndStopProgress(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mDownloadEncoding = true;
                showDebug(error.toString());
                checkSyncAndStopProgress(false);
            }
        });

    }

    private boolean mUploadAttendance = true;
    private void uploadAttendance() {
        final List<Attendance> attendances = mDbHelper.getUnsentAttendance();
        if(attendances.size()<=0) return;
        mUploadAttendance = false;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(3, 1000);
        RequestParams rparams = new RequestParams();
        String jAttendances = new Gson().toJson(attendances);
        rparams.add("attendances", jAttendances);
        String url = URL_HTTP + mIpAndPort + URL_POST_UNSENT_ATTENDANCES;
        client.post(url, rparams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mUploadAttendance = true;
                updateSentAttendance(attendances);
                checkSyncAndStopProgress(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mUploadAttendance = true;
                showDebug(error.toString());
                checkSyncAndStopProgress(false);
            }
        });
    }

    private void updateSentAttendance(List<Attendance> attendances) {
        mDbHelper.updateSentAttendances(attendances);
    }

    private void disableMenu() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloatingActionMenu fmenu = findViewById(R.id.fab_menu);
                fmenu.setEnabled(false);
                FloatingActionButton fsync = findViewById(R.id.fab_sync);
                fsync.setEnabled(false);
                FloatingActionButton ftimeout = findViewById(R.id.fab_face);
                ftimeout.setEnabled(false);
                FloatingActionButton fab_speech = findViewById(R.id.fab_speech);
                fab_speech.setEnabled(false);
                FloatingActionButton fab_location = findViewById(R.id.fab_location);
                fab_location.setEnabled(false);
                FloatingActionButton fab_add_user = findViewById(R.id.fab_add_user);
                fab_add_user.setEnabled(false);
                FloatingActionButton fab_start = findViewById(R.id.fab_start);
                fab_start.setEnabled(false);
            }
        });
    }

    private void enableMenu() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloatingActionMenu fmenu = findViewById(R.id.fab_menu);
                fmenu.setEnabled(true);
                FloatingActionButton fsync = findViewById(R.id.fab_sync);
                fsync.setEnabled(true);
                FloatingActionButton ftimeout = findViewById(R.id.fab_face);
                ftimeout.setEnabled(true);
                FloatingActionButton fab_speech = findViewById(R.id.fab_speech);
                fab_speech.setEnabled(true);
                FloatingActionButton fab_location = findViewById(R.id.fab_location);
                fab_location.setEnabled(true);
                FloatingActionButton fab_add_user = findViewById(R.id.fab_add_user);
                fab_add_user.setEnabled(true);
                FloatingActionButton fab_start = findViewById(R.id.fab_start);
                fab_start.setEnabled(true);
            }
        });
    }

    private void promptPasswordDebug() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final EditText edtPassword = dialogView.findViewById(R.id.edt_password);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = edtPassword.getText().toString().trim();
                if(password.equals(getDebugPassword()))
                    onDebugClick();
            }
        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        dialog.show();
        mFabMenu.close(false);
    }

    private String getDebugPassword() {
        return ("2410");
    }

    private void doPingAndSync() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1,1000);
        client.setResponseTimeout(1000);
        client.setConnectTimeout(1000);
        String url = URL_HTTP + mIpAndPort + URL_GET_PING;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                doManualSync();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                displayToastError(null, "Cannot connect to server");
                displayToastError(null, "Tidak terkoneksi ke server.");
            }
        });
    }

    private void doPingAndPost(final DetectionProto.Detection detection) {
        showDebug("doPingAndPost");
        mIsProcessing = true;
        startAnim = true;
        displayProgressSpinKit();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1,1000);
        client.setResponseTimeout(1000);
        client.setConnectTimeout(1000);
        String url = URL_HTTP + mIpAndPort + URL_GET_PING;
        showDebug(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mIsOnline = true;
                processFrame(detection, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mIsOnline = false;
//                showDebug("doPingAndPost2");
                processFrame(detection, false);
            }
        });
    }

    private void predictOffline(final Bitmap croppedBitmap) {
        Log.d(TAG, "Entering Predict offline");
        showDebug("Entering Predict offline");
        final long start = SystemClock.uptimeMillis();
        byte[] byteArrayPhoto = Utils.getBitmapAsByteArray(croppedBitmap);
        String encodedFile = Utils.getByteArrayAsString64(byteArrayPhoto);
        if (!Python.isStarted()) {
            displayToastError(null, "Internal error, cannot start compiler engine");
            delayedFinishProcessFlag();
            hideScanAnim();
            return;
        }
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule("encoder");
        PyObject ret = pythonFile.callAttr("encode", encodedFile);
        float[] encoding = ret.toJava(float[].class);
        List<Encoding> encodings = mDbHelper.getAllEncodings();
        if(encodings.size()==0) {
            showDebug("No encoding record in database. Quit.");
            hideProgressSpinKit();
            hideTextProgress();
            displayBottomMessageError(null, "Tidak ada data di database.");
            hideScanAnim();
            return;
        }
        Prediction prediction = new Prediction(encoding, encodings, mThresholdDistanceFaceEmbedding);
        prediction.process();
        String userMatch = prediction.getMatchUserId();
        float dist = prediction.getMatchDistance();
        String name = mDbHelper.getUserName(userMatch);
        if(userMatch.isEmpty()) {
            showDebug("No Match");
            hideScanAnim();
        } else {
            String msg = ">>> " + userMatch + ":" + name + ":" + dist + ", ";
//            showDebug(msg);
        }
        long stop = SystemClock.uptimeMillis();
//        showDebug("Timecost: " + (stop-start));
        if((mDebug && !mPostAttendanceDuringDebug) || mNoPostAttendance) {
            // debug is on and no need post attendance then quit
            showDebug("DEBUG ON and POST ATTENDANCE OFF. No posting attendance to server.");
            hideProgressSpinKit();
            hideTextProgress();
            hideScanAnim();
            if(userMatch.isEmpty()) {
                displayBottomMessageUnknown();
                speakFeedback("Tidak dikenal.");
            } else {
                displayBottomMessageSuccess("Halo " + name + " (NIK: " + userMatch + ")");
            }
            return;
        }
        if(!userMatch.isEmpty()) {
            mUsernamePostAttendance = name;
            mUserIdPostAttendance = userMatch;
            mIsOnline = false;
            if (mAutoMode) {
                autoModeOffline(userMatch, name, croppedBitmap);
            } else {
                mBitmapBig2 = mBitmapBig;
                manualMode(userMatch, name, /*croppedBitmap*/mBitmapBig, false);
            }
        } else {
            /**
             No face match. Quit.
             */
            speakFeedback("Tidak dikenal.");
            displayBottomMessageUnknown();
            showDebug("No face match");
            hideProgressSpinKit();
            hideTextProgress();
            hideScanAnim();
            return;
        }
    }

    private Bitmap mBitmapBig2;
    private void predictOnline(final Bitmap croppedBitmap) {
        Log.d(TAG, "Entering Predict online");
        showDebug("Entering Predict online");
        final long start = SystemClock.uptimeMillis();
        byte[] byteArrayPhoto = Utils.getBitmapAsByteArray(croppedBitmap);
        String encodedFile = Utils.getByteArrayAsString64(byteArrayPhoto);
        String url = URL_HTTP + mIpAndPort + URL_GET_PREDICTION;
        RequestParams rparams = new RequestParams();
        rparams.put("thumb", encodedFile);
        rparams.put("threshold", mThresholdDistanceFaceEmbedding);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(2, 1000);
        client.setResponseTimeout(3000);
        showDebug(url);
        client.get(url, rparams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200) {
                    String msg = ">>> ";
                    String name = "";
                    String user_id = "";
                    String firstName = "";
                    String firstUserid = "";
                    boolean emptyEncoding = false;
                    try {
                        String str = new String(responseBody);
                        if(str.equalsIgnoreCase("empty encoding"))
                            emptyEncoding = true;
                        JSONObject jsonResponse = new JSONObject(str);
                        int numel = 3;
                        for(int i=0;i<numel;i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(Integer.toString(i+1));
                            user_id = jsonObject.getString("user_id");
                            name = jsonObject.getString("name");
                            String dist = jsonObject.getString("distance");
                            msg = msg + user_id + ":" + name + ":" + dist + ", ";
                            if(i==0) {
                                firstName = name;
                                firstUserid = user_id;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        showDebug("JSON Exception: " + e.toString());
                        // Exception here is just to catch Json number because we depend on this exception
                        // so dont set flag processing
                    } finally {
                        if(firstUserid.isEmpty()) {
                            if(emptyEncoding)
                                showDebug("Database encoding empty in server");
                            else
                                showDebug("No Match");
                            hideScanAnim();
                        } else {
                            showDebug(msg);
                        }
                        long stop = SystemClock.uptimeMillis();
                        showDebug("Timecost: " + (stop-start));
                        if((mDebug && !mPostAttendanceDuringDebug) || mNoPostAttendance) {
                            // debug is on and no need post attendance then quit
                            showDebug("DEBUG ON and POST ATTENDANCE OFF. No posting attendance to server.");
//                            mIsProcessing = false;
                            hideScanAnim();
                            hideProgressSpinKit();
                            hideTextProgress();
                            if(firstUserid.isEmpty()) {
                                if(emptyEncoding) {
                                    displayToastError(null, "Tidak ada record di database");
                                } else {
                                    displayToastError(null, "Tidak dikenal.");
                                    speakFeedback("Tidak dikenal.");
                                }
                                delayedFinishProcessFlag();

                            } else {
                                displayBottomMessageSuccess("Halo " + firstName + " (NIK: " + firstUserid + ")");
                                speakFeedback("Halo " + firstName);
                            }
                            return;
                        }
                        if(!firstUserid.isEmpty()) {
                            mUsernamePostAttendance = firstName;
                            mUserIdPostAttendance = firstUserid;
                            if (mAutoMode) {
                                autoModeOnline(firstUserid, firstName, /*croppedBitmap*/ mBitmapBig);
                            } else {
                                mBitmapBig2 = mBitmapBig;
                                manualMode(firstUserid, firstName, /*croppedBitmap*/ mBitmapBig, true);
                            }
                        } else {
                            /**
                            No face match. Quit.
                             */
                            speakFeedback("Tidak dikenal.");
                            displayBottomMessageUnknown();
                            showDebug("No face match");
                            hideProgressSpinKit();
                            hideTextProgress();
                            hideScanAnim();
                            return;
                        }
                    }
                } else {
                    // error on getting prediction, URL = URL_GET_PREDICTION
                    // success but not return 200, nothing we have here, repeat the process
                    showDebug("Prediction success but response not 200, should not be happened");
                    mIsProcessing = false;
//                    startAnim = false;
                    hideProgressSpinKit();
                    hideTextProgress();
                    hideScanAnim();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode==404) {
                    String payload = new String(responseBody);
                    try {
                        JSONObject jsonResponse = new JSONObject(payload);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        displayBottomMessageError(null, "Server message: " + message);
                        hideProgressSpinKit();
                    } catch (JSONException e) {
                        displayToastError(null, "Unknown error.");
                        hideProgressSpinKit();
                        e.printStackTrace();
                    }
                } else {
                    showDebug("Call API fail: ");
                    showDebug("Statuscode: " + Integer.toString(statusCode));
                    showDebug("Message: " + error.toString());
                    displayBottomMessageError(null, "Koneksi bermasalah dengan server.");
//                mIsProcessing = false;
                    hideProgressSpinKit();
                }
                hideScanAnim();
            }
        });
    }

//    private int mSpeakCounter = 0;
    private void manualMode(final String userId, final String name,
                            final Bitmap croppedBitmap, final boolean online) {
        // MANUAL MODE
        showDebug("Entering Attendance process offline");
        // in NON AUTOMODE, user is asked to confirm everytime before sending data to DB
        // no need to check last record IN/OUT because user is requested to provide the IN/OUT
        hideScanAnim();
//        playDing();
        displayBottomAttendanceConfirmUIThread(userId, name, croppedBitmap, online); //mBitmapBig, online);
        showBlurBackground();
        startListeningVoiceCommandUIThread();
    }

    private boolean requestBlurBackground = false;
    private void showBlurBackground() {
        requestBlurBackground = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageViewBlur.setImageBitmap(mBitmapScreen);
                mImageViewBlur.setVisibility(View.VISIBLE);
                mBlurView.setVisibility(View.VISIBLE);

            }
        });

    }

    private void hideBlurBackground() {
        requestBlurBackground = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageViewBlur.setVisibility(View.INVISIBLE);
                mBlurView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void drawBitmapBlur() {
        if(requestBlurBackground && mBitmapScreen!=null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageViewBlur.setImageBitmap(mBitmapScreen);
                }
            });

        }
    }

    private String mUserIdPostAttendance;
    private String mUsernamePostAttendance;
    private boolean mIsOnline = false;

    private void playDing() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mPlayer = MediaPlayer.create(mContext, R.raw.pristine2);
                mPlayer.setLooping(false);
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mute();
                    }
                });
                mPlayer.start();
            }
        });

    }

    private void startListeningVoiceCommandUIThreadNoSound() {
        if(mVoiceCommand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mute();
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    showDebug("Start listening called");
                }
            });
        }
    }

    private void startListeningVoiceCommandUIThread() {
        if(mVoiceCommand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mute();
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    showDebug("Start listening called");
                    //

                }
            });
        }
    }

    private SpinKitView mProgressVoice;



    private void stopListeningVoiceCommand() {

        if(mVoiceCommand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    unMute();
                    mSpeechRecognizer.stopListening();
                    showDebug("Stop listening called");
                    //

                }
            });
        }
    }

    private void speakWelcome(String name) {
        speakFeedback("Selamat datang " + name);
    }

    private void speakGoodbye(String name) {
        speakFeedback("Sampai jumpa " + name);
    }

    private void speakNoProcess() {
        speakFeedback("Maaf, tidak dapat memproses absensi");
    }

    private void autoModeOffline(final String userId, final String name, final Bitmap croppedBitmap) {
        // automode but offline
        showDebug("Entering Offline AutoMode");
        showTextProgress("Mengambil data kehadiran terakhir dari database...");
        showDebug("Getting last attendance data from local DB of this user....");
        // current datetime
        String createdAtNow = new DateUtils("-").getCurrentDate();
        String createdOnNow = new DateUtils("-").getCurrentTime();
        final long createdDateTimeNow = new DateUtils("-").stringToEpoch(createdAtNow + " " + createdOnNow);
        // get last attandance from SQLite
        Attendance lastAttendance = mDbHelper.getLastAttendance(userId);
        // construct
        Attendance attendanceIn = new Attendance(userId, createdDateTimeNow, STRING_CLOCK_IN, mLocation, croppedBitmap, "X");
        Attendance attendanceOut = new Attendance(userId, createdDateTimeNow, STRING_CLOCK_OUT, mLocation, croppedBitmap, "X");
//        boolean repeat = false;
        if(lastAttendance==null) {
            // this is the first time this guy doing attendance
            insertAttendance(attendanceIn);
            showBlurBackground();
            displayBottomAttendanceOkUIThread(/*mCroppedBitmap*/mBitmapBig, name, userId, STRING_CLOCK_IN, false);
            speakWelcome(name);
            hideProgressSpinKit();
            hideScanAnim();
        } else {
            // not the first time, check autotimeout first
            String createdAtLast = lastAttendance.getCreatedAt();
            createdAtLast = createdAtLast.replace("-", "/");
            String createdOnLast = lastAttendance.getCreatedOn();
            String[] split = createdOnLast.split(":");
            String createdOn = split[0] + ":" + split[1];
//            repeat = true;
            Long mAutoTimeout_inMillis = Long.valueOf(
                    (mAutoTimeoutMinutes * 60 * 1000)       // minutes
                    + (mAutoTimeoutHours * 60 * 60 * 1000)  // hours
            );
            String msg = "";
            if(createdDateTimeNow-lastAttendance.getEpochDateTime()<mAutoTimeout_inMillis) {
                // eh, lo mau clockin/out lagi tong, baru bentaran lo
                if(mAutoTimeoutHours==0) {
                    msg = "NIK: " + userId + CARRIAGE_RETURN + "Nama: " + name + CARRIAGE_RETURN + "Maaf, Anda hanya dapat melakukan absensi lagi setelah "
                            + mAutoTimeoutMinutes
                            + " menit. Terakhir melakukan absensi pada "
                            + createdAtLast + " " + createdOn;
                } else {
                    if(mAutoTimeoutMinutes==0) {
                        msg = "NIK: " + userId + CARRIAGE_RETURN + "Nama: " + name + CARRIAGE_RETURN + "Maaf, Anda hanya dapat melakukan absensi lagi setelah "
                                + mAutoTimeoutHours
                                + " jam. Terakhir melakukan absensi pada "
                                + createdAtLast + " " + createdOn;
                    } else {
                        msg = "NIK: " + userId + CARRIAGE_RETURN + "Nama: " + name + CARRIAGE_RETURN + "Maaf, Anda hanya dapat melakukan absensi lagi setelah "
                                + mAutoTimeoutHours + " jam "
                                + mAutoTimeoutMinutes
                                + " menit. Terakhir melakukan absensi pada "
                                + createdAtLast + " " + createdOn;
                    }
                }
                msg = msg + " WIB";
                displayBottomMessageWarning(msg);
                hideProgressSpinKit();
                hideScanAnim();
                speakNoProcess();
            } else {
                // more than AutoTimeout
                if (lastAttendance.getStatus().equalsIgnoreCase(STRING_CLOCK_IN)) {
                    // do clock out
                    insertAttendance(attendanceOut);
                    showBlurBackground();
                    displayBottomAttendanceOkUIThread(/*mCroppedBitmap*/mBitmapBig, name, userId, STRING_CLOCK_OUT, false);
                    hideProgressSpinKit();
                    hideScanAnim();
                    speakGoodbye(name);
                } else {
                    // do clock in
                    insertAttendance(attendanceIn);
                    showBlurBackground();
                    displayBottomAttendanceOkUIThread(/*mCroppedBitmap*/mBitmapBig, name, userId, STRING_CLOCK_IN, false);
                    hideProgressSpinKit();
                    hideScanAnim();
                    speakWelcome(name);
                }

            }
        }

    }

    private void autoModeOnline(final String userId, final String name, final Bitmap croppedBitmap) {
        // AUTOMODE
        /**
         * This is the important feature of FaceSecure: to decide automatically whether
         * this user is clock in or clock out. Decision (IN.OUT) is based on last attendance data.
         * If last attendance data is IN then this is OUT, and vice versa
         */
        showDebug("Entering Online AutoMode");
        showTextProgress("Mengambil data kehadiran terakhir dari database...");
        showDebug("Getting last attendance data from server of this user....");
        /**
         * GET LAST ATTENDANCE
         */
        String url = URL_HTTP + mIpAndPort + URL_GET_LAST_ATTENDANCE;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(2, 1000);
        client.setResponseTimeout(5000);
        RequestParams rparams = new RequestParams();
        rparams.add("user_id", userId);
        showDebug(url);
        client.get(url, rparams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    showDebug("Response valid 200");
                    String response = new String(responseBody);
                    if (response.contains("NA")) {
                        // so this user is first time doing attendance
                        postAttendanceOnline(userId, name, STRING_CLOCK_IN, croppedBitmap);
                    } else {
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            String status = jo.getString("status");
                            String name = jo.getString("name");
                            String strDateAttendance = jo.getString("created_at"); //sep[0].trim();
                            String strTimeAttendance = jo.getString("created_on"); //sep[1].trim();
                            String strAttDateTime = strDateAttendance + " " + strTimeAttendance;
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date lastAttDateTime = dateFormat.parse(strAttDateTime);
                            Long lastAt_millis = lastAttDateTime.getTime();
                            Date dateNow = Calendar.getInstance().getTime(); // get now time
                            Long nowTime_millis = dateNow.getTime();
                            // convert minutes to millis for AutoTimeout
                            Long mAutoTimeout_inMillis = Long.valueOf((mAutoTimeoutHours * 60 * 60 * 1000) + (mAutoTimeoutMinutes * 60 * 1000));
                            if ((nowTime_millis - lastAt_millis) < mAutoTimeout_inMillis) {
                                // eh, lo mau clockin/out lagi tong, baru bentaran lo
                                String msg = "";
                                if(mAutoTimeoutHours==0) {
                                    msg = "NIK: " + userId + CARRIAGE_RETURN + name + CARRIAGE_RETURN + "Maaf, Anda hanya dapat melakukan absensi lagi setelah "
                                            + mAutoTimeoutMinutes
                                            + " menit. " + CARRIAGE_RETURN + "Terakhir melakukan absensi pada "
                                            + strDateAttendance + " " + strTimeAttendance;
                                } else {
                                    if(mAutoTimeoutMinutes==0) {
                                        msg = "NIK: " + userId + CARRIAGE_RETURN + name + CARRIAGE_RETURN + "Maaf, Anda hanya dapat melakukan absensi lagi setelah "
                                                + mAutoTimeoutHours
                                                + " jam. " + CARRIAGE_RETURN + "Terakhir melakukan absensi pada "
                                                + strDateAttendance + " " + strTimeAttendance;
                                    } else {
                                        msg = "NIK: " + userId + CARRIAGE_RETURN + name + CARRIAGE_RETURN + "Maaf, Anda hanya dapat melakukan absensi lagi setelah "
                                                + mAutoTimeoutHours + " jam "
                                                + mAutoTimeoutMinutes
                                                + " menit. " + CARRIAGE_RETURN + "Terakhir melakukan absensi pada "
                                                + strDateAttendance + " " + strTimeAttendance;
                                    }
                                }
                                msg = msg + " WIB";
                                displayBottomMessageWarning(msg);
                                hideProgressSpinKit();
                                hideScanAnim();
                                speakNoProcess();

                            } else {
                                // more than AutoTimeout
                                if (status.equalsIgnoreCase(STRING_CLOCK_IN)) {
                                    postAttendanceOnline(userId, name, STRING_CLOCK_OUT, croppedBitmap);
                                } else {
                                    postAttendanceOnline(userId, name, STRING_CLOCK_IN, croppedBitmap);
                                }
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            showDebug(e.toString());
                            mIsProcessing = false;
//                            startAnim = false;
                            hideProgressSpinKit();
                            hideScanAnim();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showDebug(error.toString());
                displayBottomMessageError(error, "Internal Error");
//                mIsProcessing = false;
                hideProgressSpinKit();
                hideScanAnim();
            }
        });
    }

    private String mLocation = "00";
    private void postAttendanceOnline(final String userId, String name, final String status, final Bitmap croppedBitmap) {
        showDebug("Post attendance data: " + status);
        showTextProgress("Menyimpan data kehadiran ke database");
        // current date/time
        String createdAt = new DateUtils("-").getCurrentDate();
        String createdOn = new DateUtils("-").getCurrentTime();
        final long createdDateTime = new DateUtils("-").stringToEpoch(createdAt + " " + createdOn);
        Bitmap bitmapRescale = Utils.scaleImageKeepAspectRatio(croppedBitmap, Utils.IMG_MAX_WIDTH);
        byte[] byteArray = Utils.getBitmapAsByteArray(bitmapRescale);
        String encodedThumb = Utils.getByteArrayAsString64(byteArray);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(2, 1000);
        client.setResponseTimeout(5000);
        RequestParams rparams = new RequestParams();
        rparams.add("user_id", userId);
        rparams.add("status", status);
        rparams.add("location", mLocation);
        rparams.add("created_at", createdAt);
        rparams.add("created_on", createdOn);
        rparams.put("thumb", encodedThumb);
        String url = URL_HTTP + mIpAndPort + URL_POST_ATTENDANCE;
        showDebug("Post to URL: " + url);

        client.post(url, rparams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                try {
                    JSONObject jo = new JSONObject(response);
                    String resultName = jo.getString("name");
                    String resultUserid = jo.getString("user_id");
                    String resultStatus = jo.getString("status");
                    showBlurBackground();
                    if (resultStatus.equalsIgnoreCase(STRING_CLOCK_IN)) {
                        displayBottomAttendanceOkUIThread(mBitmapBig, name, userId, STRING_CLOCK_IN, true);
                        speakWelcome(resultName);
                    } else {
                        displayBottomAttendanceOkUIThread(mBitmapBig, name, userId, STRING_CLOCK_OUT, true);
                        speakGoodbye(resultName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Attendance attendance = new Attendance(userId, createdDateTime, status, mLocation, croppedBitmap, "");
                insertAttendance(attendance);
                hideProgressSpinKit();
                hideTextProgress();
                hideScanAnim();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Attendance attendance = new Attendance(userId, createdDateTime, status, mLocation, croppedBitmap, "X");
                insertAttendance(attendance);
                showBlurBackground();
                displayBottomAttendanceOkUIThread(mBitmapBig, name, userId, status, false);
                String er = error.toString();
                showDebug("Error: postAttendance-onFailure-status=" + statusCode + ",error=" + er);
                hideProgressSpinKit();
                hideTextProgress();
                hideScanAnim();
            }

        });
    }

    private void displayBottomAttendanceOkUIThread(Bitmap face, String name, String nik, String atType, boolean online) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayBottomAttendanceOk(face, name, nik, atType, online);
            }
        });
    }

    private synchronized void insertAttendance(Attendance attendance) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDbHelper.insertAttendance(attendance);
            }
        });
    }

    private void screenOff() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayoutBlack.setVisibility(View.VISIBLE);
            }
        });
    }

    private void screenOn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayoutBlack.setVisibility(View.GONE);
            }
        });
    }

    public void showDebug(String msg) {
        if(mDebug) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mDebugLog!=null && mDebugAdapter!=null) {
                        mDebugLog.add(new DebugLog(msg));
                        mDebugAdapter.notifyDataSetChanged();
                        mListView.setSelection(mDebugAdapter.getCount()-1);
                    }
                }
            });
        }
    }


    public void onClearDebugList(View view) {
        mDebugLog.clear();
        mDebugAdapter.notifyDataSetChanged();
    }

    private void showLog(final String msg) {
        Log.d(TAG, msg);
    }

    private void setBitmap(Bitmap bmp) {
        if(mDebug) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Stuff that updates the UI
                    imView.setImageBitmap(bmp);
                }
            });
        }
    }

    private void onVoiceClick() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_voice_id, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final CheckBox ckAutoMode = dialogView.findViewById(R.id.ck_auto);
        final CheckBox ckVoiceCommand = dialogView.findViewById(R.id.ck_command);
        final CheckBox ckVoiceFeedback = dialogView.findViewById(R.id.ck_feedback);
        final MaskedEditText edtAutoTimeoutForbidden = dialogView.findViewById(R.id.edt_timeout);
        final EditText edtWaitingVoice = dialogView.findViewById(R.id.edt_waiting_voice);
        ckAutoMode.setChecked(mAutoMode);
        ckVoiceCommand.setChecked(mVoiceCommand);
        ckVoiceFeedback.setChecked(mVoiceFeedback);
        ckAutoMode.setEnabled(true);
        ckVoiceCommand.setEnabled(!mAutoMode);
        edtWaitingVoice.setEnabled(ckVoiceCommand.isChecked());
        edtWaitingVoice.setText(Integer.toString(mVoiceCommandWaitingTimer));
        String h=""; String m="";
        // put prefix 0 for the string to be displayed
        if(mAutoTimeoutHours<10) h = "0" + Integer.toString(mAutoTimeoutHours);
        else h=Integer.toString(mAutoTimeoutHours);
        if(mAutoTimeoutMinutes<10) m = "0" + Integer.toString(mAutoTimeoutMinutes);
        else m=Integer.toString(mAutoTimeoutMinutes);
        edtAutoTimeoutForbidden.setText(h + ":" + m);
        edtAutoTimeoutForbidden.setEnabled(mAutoMode);
        ckAutoMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ckVoiceCommand.setEnabled(!isChecked);
                edtAutoTimeoutForbidden.setEnabled(isChecked);
            }
        });
        ckVoiceCommand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edtWaitingVoice.setEnabled(isChecked);
            }
        });
        NoboButton btnSave = dialogView.findViewById(R.id.btn_voice_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutoMode = ckAutoMode.isChecked(); Prefs.putBoolean(PREF_AUTO_MODE,mAutoMode);
                mVoiceCommand = ckVoiceCommand.isChecked(); Prefs.putBoolean(PREF_VCOMMAND, mVoiceCommand);
                mVoiceFeedback = ckVoiceFeedback.isChecked(); Prefs.putBoolean(PREF_VFEEDBACK, mVoiceFeedback);
                String textTimeForbidden = edtAutoTimeoutForbidden.getText().toString().trim();
                int hour = 0; int minute = 0;
                if(mAutoMode) {
                    // auto mode
                    // evaluate auto mode timeout for same person
                    if(!textTimeForbidden.isEmpty()) {
                        String[] mh = textTimeForbidden.split(":");
                        try {
                            hour = Integer.parseInt(mh[0]);
                            if(mh.length==2) {
                                minute = Integer.parseInt(mh[1]);
                            }
                        } catch(Exception ex) {

                        }
                        if(minute>59 && mAutoMode) {
                            displayToastError(null, "Menit tidak boleh lebih dari 59");
//                            dialog.dismiss();
                            return;
                        }
                    }
                    mAutoTimeoutMinutes = minute;
                    mAutoTimeoutHours = hour;
                    Prefs.putInt(PREF_AUTO_TIMEOUT_MINUTE, mAutoTimeoutMinutes);
                    Prefs.putInt(PREF_AUTO_TIMEOUT_HOUR, mAutoTimeoutHours);
                } else {
                    // not auto mode
                    // evaluate waiting time for voice command
                    String textWaitingVoiceCommand = edtWaitingVoice.getText().toString().trim();
                    if(textWaitingVoiceCommand.isEmpty()) {
                        displayBottomMessageError(null, "Waktu tunggu perintah suara tidak boleh kosong");
                        return;
                    }
                    int waitingTime = Integer.parseInt(textWaitingVoiceCommand);
                    if(waitingTime==0) {
                        displayBottomMessageError(null, "Waktu tunggu perintah suara tidak boleh 0");
                        return;
                    }
                    mVoiceCommandWaitingTimer = waitingTime;
                    Prefs.putInt(ARGS_TIMER_WAITING_VOICE_COMMAND, waitingTime);
                }

                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        NoboButton btnCancel = dialogView.findViewById(R.id.btn_voice_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        dialog.show();
    }

    private void onAddUser() {
        displayProgressGreen();
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);
    }

    private void openTestActivity() {
        Intent intent = new Intent(MainActivity.this, GeneralPurposeActivity.class);
        startActivity(intent);
    }

    private void onLocationClick() {
        new LovelyTextInputDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle("Pengaturan Terminal ID")
                .setMessage("Terminal ID:")
                .setIcon(R.drawable.ic_location)
                .setInitialInput(mLocation)
                .setCancelable(false)
                .setConfirmButtonColor(getResources().getColor(R.color.colorPrimaryDark))
                .setNegativeButtonColor(getResources().getColor(R.color.colorRed))
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        mLocation = text;
                        Prefs.putString(PREF_TERMINAL_LOCATION, mLocation);
//                        hideBlurBackground();
                    }
                })
                .setNegativeButton("Batal", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        hideBlurBackground();
                    }
                })
                .show();
    }

    private void onDebugClick() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_debug, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final CheckBox ck1 = dialogView.findViewById(R.id.ck_debug_mode);
        final CheckBox ck2 = dialogView.findViewById(R.id.ck_post_attendance);
        final CheckBox ck3 = dialogView.findViewById(R.id.ck_nopost_attendance);
        ck1.setChecked(mDebug);
        ck2.setChecked(mPostAttendanceDuringDebug);
        ck3.setChecked(mNoPostAttendance);
        if(ck3.isChecked()) ck2.setChecked(false);
        final EditText edtFaceDistance = dialogView.findViewById(R.id.edt_threshold_face_distance);
        edtFaceDistance.setText(Float.toString(mThresholdDistanceFaceEmbedding));
        final EditText edtThresholdLiveness = dialogView.findViewById(R.id.edt_threshold_liveness);
        edtThresholdLiveness.setText(Float.toString(mThresholdLiveness));
        final EditText edtDelayedFinish = dialogView.findViewById(R.id.edt_delayed_finish);
        edtDelayedFinish.setText(Integer.toString(mDelayTimeBetweenProcess));
        NoboButton btnExit = dialogView.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ck3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) ck2.setChecked(false);
            }
        });

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDebug = ck1.isChecked();
                mPostAttendanceDuringDebug = ck2.isChecked();
                mNoPostAttendance = ck3.isChecked();
                if(!edtFaceDistance.getText().toString().trim().isEmpty()) {
                    mThresholdDistanceFaceEmbedding = Float.parseFloat(edtFaceDistance.getText().toString().trim());
                    Prefs.putFloat(PREF_THRESHOLD_FACE_DISTANCE, mThresholdDistanceFaceEmbedding);
                }
                if(!edtThresholdLiveness.getText().toString().trim().isEmpty()) {
                    mThresholdLiveness = Float.parseFloat(edtThresholdLiveness.getText().toString().trim());
                    Prefs.putFloat(PREF_THRESHOLD_LIVENESS, mThresholdLiveness);
                }
                if(mDebug)
                    showDebugListView();
                else
                    hideDebugListView();
                dialog.dismiss();
//                hideBlurBackground();
            }
        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        dialog.show();
    }

    private void displayBottomMessageSuccess(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayBottomMessage(MESSAGE_SUCCESS, message);
            }
        });
    }

    private void displayToastSuccess(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DynamicToast.makeSuccess(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayBottomMessageWarning(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayBottomMessage(MESSAGE_WARNING, message);
            }
        });
    }

    private void displayToastWarning(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DynamicToast.makeWarning(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayBottomMessageError(Throwable throwable, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(throwable!=null && mDebug == true) {
                    DynamicToast.makeError(getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                    delayedFinishProcessFlag();
                } else {
                    displayBottomMessage(MESSAGE_ERROR, message);
                }
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

    private void displayBottomMessageUnknown() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayBottomMessage(MESSAGE_PERSON_UNKNOWN, "Tidak dikenal");
            }
        });
    }

    private CountDownAnimation mCountDownAnimation;
    private void displayBottomAttendanceConfirmUIThread(String nik, String name, Bitmap face, boolean online) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayBottomAttendanceConfirm(nik, name, face, online);
            }
        });
    }

    private void displayBottomAttendanceConfirm(String nik, String name, Bitmap face, boolean online) {

        // get the layout of attendance confirmation
        showDebug("Display Attendance confirmation");
        RelativeLayout layoutAttendanceConfirm = findViewById(R.id.layout_bottom_att_confirm_c);
        TextView textCountdown = findViewById(R.id.text_counter);

        // the countdown
        mCountDownAnimation = new CountDownAnimation(textCountdown, mVoiceCommandWaitingTimer);
        mCountDownAnimation.setCountDownListener(new CountDownAnimation.CountDownListener() {
            @Override
            public void onCountDownEnd(CountDownAnimation animation) {
                showDebug("Counter done.");
                onAttendanceConfirmCounterEnd();
                dismissAttendanceConfirm();

            }
        });

        // set name and nik
        TextView textViewName = findViewById(R.id.tv_name_c);
        textViewName.setText(name);
        TextView textViewId = findViewById(R.id.tv_nik_c);
        textViewId.setText(nik);
        // face
        CircleImage ci = findViewById(R.id.iv_thumb_c);
        ci.setImageBitmap(face);

        // set date and time
        TextView textViewDate = findViewById(R.id.tv_date_c);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        Date d = new Date();
        String formattedCurrentDate = df.format(d);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(d);
        textViewDate.setText(dayOfTheWeek + ", " + formattedCurrentDate);
        TextView textViewTime = findViewById(R.id.tv_time_c);
        SimpleDateFormat sdft = new SimpleDateFormat("HH:mm");
        String textTime = sdft.format(d);
        textViewTime.setText(textTime + " WIB");

        // set online/offline
        CompoundIconTextView textViewOffline = findViewById(R.id.tv_offline_c);
        CompoundIconTextView textViewOnline = findViewById(R.id.tv_online_c);
        textViewOffline.setVisibility(View.GONE);
        textViewOnline.setVisibility(View.GONE);
        if(online) {
            textViewOnline.setVisibility(View.VISIBLE);
        } else {
            textViewOffline.setVisibility(View.VISIBLE);
        }

        layoutAttendanceConfirm.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(500)
                .playOn(layoutAttendanceConfirm);

        // start countdown
        mCountDownAnimation.start();

        ImageView btnClockin = findViewById(R.id.image_clock_in);
        btnClockin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBottomSheetButtonClick(nik, name, STRING_CLOCK_IN, online, face);
            }
        });

        ImageView btnClockout = findViewById(R.id.image_clock_out);
        btnClockout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBottomSheetButtonClick(nik, name, STRING_CLOCK_OUT, online, face);
            }
        });
    }

    private void stopCountdownTimer() {
        if(mCountDownAnimation !=null) {
            mCountDownAnimation.cancel();
        }
    }

    private void dismissAttendanceConfirm() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout layoutAttendanceConfirm = findViewById(R.id.layout_bottom_att_confirm_c);
                YoYo.with(Techniques.SlideOutDown)
                        .duration(500)
                        .playOn(layoutAttendanceConfirm);
                stopCountdownTimer();
            }
        });
    }

    private void displayBottomAttendanceOk(Bitmap face, String name, String nik, String aType, boolean online) {
        //
        if(aType.equalsIgnoreCase(STRING_CLOCK_OUT)) {
            LottieDrawable lottieDrawable = new LottieDrawable();
            lottieDrawable.setMaxFrame(60);
            lottieDrawable.setMinFrame(30);
            lottieDrawable.setSpeed(0.5f);
            ImageView imageAnim = findViewById(R.id.image_lottie);
            imageAnim.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            imageAnim.setImageDrawable(lottieDrawable);
            LottieComposition.Factory.fromAssetFileName(mContext, "anim_clock_in_out.json", (composition) -> {
                lottieDrawable.setComposition(composition);
                lottieDrawable.loop(false);
                lottieDrawable.playAnimation();
            });
        } else if(aType.equalsIgnoreCase(STRING_CLOCK_IN)) {
            LottieDrawable lottieDrawable = new LottieDrawable();
            lottieDrawable.setMaxFrame(30);
            lottieDrawable.setMinFrame(0);
            lottieDrawable.setSpeed(0.5f);
            ImageView imageAnim = findViewById(R.id.image_lottie);
            imageAnim.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            imageAnim.setImageDrawable(lottieDrawable);
            LottieComposition.Factory.fromAssetFileName(mContext, "anim_clock_in_out.json", (composition) -> {
                lottieDrawable.setComposition(composition);
                lottieDrawable.loop(false);
                lottieDrawable.playAnimation();
            });
        } else if(aType.equalsIgnoreCase(STRING_CLOCK_CANCEL)) {
            LottieDrawable lottieDrawable = new LottieDrawable();
//            lottieDrawable.setMaxFrame(30);
//            lottieDrawable.setMinFrame(0);
            lottieDrawable.setSpeed(0.5f);
            ImageView imageAnim = findViewById(R.id.image_lottie);
            imageAnim.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            imageAnim.setImageDrawable(lottieDrawable);
            LottieComposition.Factory.fromAssetFileName(mContext, "anim_cancel2.json", (composition) -> {
                lottieDrawable.setComposition(composition);
                lottieDrawable.loop(true);
                lottieDrawable.playAnimation();
            });
        }
        // layout
        LinearLayout layoutAttendanceOk = findViewById(R.id.layout_bottom_att_ok);
        layoutAttendanceOk.setVisibility(View.VISIBLE);
        //
        YoYo.with(Techniques.SlideInUp)
                .duration(500)
                .playOn(layoutAttendanceOk);

        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(name);
        TextView tvNik = findViewById(R.id.tv_nik);
        tvNik.setText(nik);
        CompoundIconTextView textViewOffline = findViewById(R.id.tv_offline);
        CompoundIconTextView textViewOnline = findViewById(R.id.tv_online);
        if(online) {
            textViewOffline.setVisibility(View.GONE);
            textViewOnline.setVisibility(View.VISIBLE);
        } else {
            textViewOffline.setVisibility(View.VISIBLE);
            textViewOnline.setVisibility(View.GONE);
        }
        TextView textViewDate = findViewById(R.id.tv_date);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        Date d = new Date();
        String formattedCurrentDate = df.format(d);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(d);
        textViewDate.setText(dayOfTheWeek + ", " + formattedCurrentDate);
        TextView textViewTime = findViewById(R.id.tv_time);
        SimpleDateFormat sdft = new SimpleDateFormat("HH:mm");
        String textTime = sdft.format(d);
        textViewTime.setText(textTime + " WIB");
        //
        CircleImage ci = findViewById(R.id.iv_thumb);
        if(mAutoMode)
            ci.setImageBitmap(face);
        else
            ci.setImageBitmap(mBitmapBig2);

        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.SlideOutDown)
                                .duration(500)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        hideBlurBackground();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                })
                                .playOn(layoutAttendanceOk);
                        delayedFinishProcessFlag();
                    }
                });
            }
        };
        handler.postDelayed(runnable, 4000);
    }

    private void onBottomSheetButtonClick(String userId, String name,
                                         String attType, boolean online, Bitmap face) {
//        hideBlurBackground();
        stopListeningVoiceCommand();
        dismissAttendanceConfirm();

        resetSpeechRecognizerUIThread();
        String createdAt = new DateUtils("-").getCurrentDate();
        String createdOn = new DateUtils("-").getCurrentTime();
//        mDialogAttendanceConfirm.dismiss();
        if(attType.equals(STRING_CLOCK_CANCEL)) {
//            mIsProcessing = false;
            hideProgressSpinKit();
            displayBottomAttendanceOk(face, name, userId, attType, online);
            speakFeedback("Dibatalkan.");
        } else if(attType.equals(STRING_CLOCK_IN)) {
            if(online) {
//                showDebug("IN clicked on offline mode");
                postAttendanceOnline(userId, name, attType, face);
            } else {
//                showDebug("IN clicked on offline mode");
                final long createdDateTime = new DateUtils("-").stringToEpoch(createdAt + " " + createdOn);
                Attendance attendance = new Attendance(userId, createdDateTime,
                        attType, mLocation, face, "X");
                insertAttendance(attendance);
//                mIsProcessing = false;
                hideProgressSpinKit();
                hideTextProgress();
//                speakFeedback("Selamat datang, " + name);
                speakWelcome(name);
                displayBottomAttendanceOk(/*face*/face, name, userId, attType, false);
            }
        } else if(attType.equals(STRING_CLOCK_OUT)) {
            if(online) {
                showDebug("OUT clicked on online mode");
                postAttendanceOnline(userId, name, attType, face);
            } else {
                showDebug("OUT clicked on offline mode");
                Attendance attendance = new Attendance(userId, SystemClock.uptimeMillis(),
                        attType, mLocation, face, "X");
                insertAttendance(attendance);
//                mIsProcessing = false;
                hideProgressSpinKit();
                hideTextProgress();
//                speakFeedback("Sampai jumpa, " + name);
                speakGoodbye(name);
                displayBottomAttendanceOk(/*face*/face, name, userId, attType, false);
            }
        }
    }

    public void onAttendanceConfirmCounterEnd() {
        stopListeningVoiceCommand();
        resetSpeechRecognizerUIThread();
//        hideBlurBackground();
        displayBottomMessageSuccess("Tidak ada respon, data kehadiran tidak diproses.");
        speakFeedback("Dibatalkan.");
        hideProgressSpinKit();
    }

    private void delayedFinishProcessFlag() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showDebug("DELAYED FINISH PROCESS FLAG");
                mIsProcessing = false;

            }
        };
        handler.postDelayed(runnable, mDelayTimeBetweenProcess);
    }

    private void displayBottomMessage(String type, String message) {

        RelativeLayout relativeLayout;
        TextView tvMsg = findViewById(R.id.bottom_message_content);
        tvMsg.setText(message);

        if(type.equalsIgnoreCase(MainActivity.MESSAGE_PERSON_UNKNOWN)) {

            relativeLayout = findViewById(R.id.layout_bottom_unknown);
            CompoundIconTextView tvOnline = findViewById(R.id.tv_online_u);
            CompoundIconTextView tvOffline = findViewById(R.id.tv_offline_u);
            if(mIsOnline) {
                tvOnline.setVisibility(View.VISIBLE);
                tvOffline.setVisibility(View.GONE);
            } else {
                tvOffline.setVisibility(View.VISIBLE);
                tvOnline.setVisibility(View.GONE);
            }

        } else {
            CompoundIconTextView tvOnline = findViewById(R.id.tv_online_m);
            CompoundIconTextView tvOffline = findViewById(R.id.tv_offline_m);
            if(mIsOnline) {
                tvOnline.setVisibility(View.VISIBLE);
                tvOffline.setVisibility(View.GONE);
            } else {
                tvOffline.setVisibility(View.VISIBLE);
                tvOnline.setVisibility(View.GONE);
            }

            relativeLayout = findViewById(R.id.layout_bottom_message);
            RelativeLayout titleLayout = findViewById(R.id.bottom_message_title);
            ImageView ivIcon = findViewById(R.id.bottom_message_icon);
            TextView msgContent = findViewById(R.id.bottom_message_content);
            msgContent.setText(message);

            if (type.equalsIgnoreCase(MainActivity.MESSAGE_ERROR)) {
//                titleLayout.setBackgroundColor(getResources().getColor(R.color.colorRed));
                titleLayout.setBackground(getDrawable(R.drawable.layout_title_error));
                ivIcon.setImageResource(R.drawable.ic_error);
            } else if (type.equalsIgnoreCase(MainActivity.MESSAGE_INFO)) {
//                titleLayout.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                titleLayout.setBackground(getDrawable(R.drawable.layout_title_info));
                ivIcon.setImageResource(R.drawable.ic_about);
            } else if (type.equalsIgnoreCase(MainActivity.MESSAGE_SUCCESS)) {
//                titleLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                titleLayout.setBackground(getDrawable(R.drawable.layout_title_success));
                ivIcon.setImageResource(R.drawable.ic_ok);
            } else if (type.equalsIgnoreCase(MainActivity.MESSAGE_WARNING)) {
//                titleLayout.setBackgroundColor(getResources().getColor(R.color.colorYellowDark));
                titleLayout.setBackground(getDrawable(R.drawable.layout_title_warning));
                ivIcon.setImageResource(R.drawable.ic_warning);
            }
        }

        showBlurBackground();
        relativeLayout.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.SlideInUp)
                .duration(500)
                .playOn(relativeLayout);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideOutDown)
                        .duration(500)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                hideBlurBackground();
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .playOn(relativeLayout)
                        ;
                delayedFinishProcessFlag();
            }
        };
        handler.postDelayed(runnable, 4000);

    }

    private void onEmailClick() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_email, null);
        //
        CheckBox ckMail = dialogView.findViewById(R.id.ck_send_email);
        ckMail.setChecked(mSendMail);
        EditText edtSmtpHost = dialogView.findViewById(R.id.edt_smtp_host);
        edtSmtpHost.setText(mSmtpHost);
        edtSmtpHost.setEnabled(ckMail.isChecked());
        EditText edtSmtpPort = dialogView.findViewById(R.id.edt_smtp_port);
        edtSmtpPort.setText(mSmtpPort);
        edtSmtpPort.setEnabled(ckMail.isChecked());
        EditText edtSmtpFrom = dialogView.findViewById(R.id.edt_smtp_send_from);
        edtSmtpFrom.setText(mSmtpSender);
        edtSmtpFrom.setEnabled(ckMail.isChecked());
        EditText edtSmtpTo = dialogView.findViewById(R.id.edt_smtp_send_to);
        edtSmtpTo.setText(mSmtpReceiver);
        edtSmtpTo.setEnabled(ckMail.isChecked());
        EditText edtSmtpPassword = dialogView.findViewById(R.id.edt_smtp_password_from);
        edtSmtpPassword.setText(mSmtpPassword);
        edtSmtpPassword.setEnabled(ckMail.isChecked());
        MaskedEditText edtEmailtime = dialogView.findViewById(R.id.edt_smtp_email_time);
        edtEmailtime.setText(mSmtpEmailTime);
        edtEmailtime.setEnabled(ckMail.isChecked());
        //
        ckMail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edtSmtpHost.setEnabled(isChecked);
                edtSmtpPort.setEnabled(isChecked);
                edtSmtpFrom.setEnabled(isChecked);
                edtSmtpTo.setEnabled(isChecked);
                edtSmtpPassword.setEnabled(isChecked);
                edtEmailtime.setEnabled(isChecked);
            }
        });
        //
        NoboButton btnSave = dialogView.findViewById(R.id.btn_email_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtSmtpHost.getText().toString().trim().isEmpty() ||
                edtSmtpPort.getText().toString().trim().isEmpty() ||
                edtSmtpFrom.getText().toString().trim().isEmpty() ||
                edtSmtpTo.getText().toString().trim().isEmpty() ||
                edtSmtpPassword.getText().toString().isEmpty() ||
                edtEmailtime.getText().toString().trim().isEmpty()) {
                    displayToastError(null, "Data tidak boleh kosong");
                    return;
                }
                String emailTime = edtEmailtime.getText().toString();
                int hour = Integer.parseInt(emailTime.split(":")[0]);
                int min = Integer.parseInt(emailTime.split(":")[1]);
                if(hour>23) {
                    displayToastError(null, "Jam tidak boleh lebih dari 23");
                    return;
                }
                if(min>59) {
                    displayToastError(null, "Menit tidak boleh lebih dari 59");
                    return;
                }

                mSmtpHost = edtSmtpHost.getText().toString();
                mSmtpPort = edtSmtpPort.getText().toString();
                mSmtpSender = edtSmtpFrom.getText().toString();
                mSmtpReceiver= edtSmtpTo.getText().toString();
                mSmtpPassword = edtSmtpPassword.getText().toString();
                mSmtpEmailTime = edtEmailtime.getText().toString();
                mSendMail = ckMail.isChecked();

                Prefs.putString(PREF_SMTP_HOST, mSmtpHost);
                Prefs.putString(PREF_SMTP_PORT, mSmtpPort);
                Prefs.putString(PREF_SMTP_SENDER_USER, mSmtpSender);
                Prefs.putString(PREF_SMTP_SENDER_PASSWORD, mSmtpPassword);
                Prefs.putString(PREF_SMTP_RECEIVER_USER, mSmtpReceiver);
                Prefs.putString(PREF_SMTP_EMAIL_SEND_TIME, mSmtpEmailTime);
                Prefs.putBoolean(PREF_SEND_MAIL, mSendMail);

                if(ckMail.isChecked()) {
                    cancelBroadcastEmailAlarm();
                    broadcastEmailAlarm();
                } else
                    cancelBroadcastEmailAlarm();

                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        NoboButton btnCancel = dialogView.findViewById(R.id.btn_email_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        DateUtils dateUtils = new DateUtils("/");
        String dateNow = dateUtils.getCurrentDate();
        EditText edtFrom = dialogView.findViewById(R.id.edt_att_from);
        edtFrom.setText(dateNow);
        EditText edtTo = dialogView.findViewById(R.id.edt_att_to);
        edtTo.setText(dateNow);
        NoboButton btnSendNow = dialogView.findViewById(R.id.btn_send_email_now);
        btnSendNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateFrom = edtFrom.getText().toString();
                String dateTo = edtTo.getText().toString();
                boolean s = sendMail(dateFrom, dateTo);
                if(!s) displayToastError(null, "Tidak ada data absensi diantara tanggal tersebut");
                else {
                    displayToastSuccess("Email terkirim");
                    dialog.dismiss();
//                    hideBlurBackground();
                }
            }
        });
        //
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.show();
    }

    private PendingIntent mAlarmIntent;
    private AlarmManager mAlarmManager;
    private void broadcastEmailAlarm() {

        int hour = Integer.parseInt(mSmtpEmailTime.split(":")[0]);
        int min = Integer.parseInt(mSmtpEmailTime.split(":")[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
//        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(mContext, EmailAlarmReceiver.class);
        intent.putExtra(PREF_SMTP_HOST, mSmtpHost);
        intent.putExtra(PREF_SMTP_PORT, mSmtpPort);
        intent.putExtra(PREF_SMTP_SENDER_USER, mSmtpSender);
        intent.putExtra(PREF_SMTP_RECEIVER_USER, mSmtpReceiver);
        intent.putExtra(PREF_SMTP_SENDER_PASSWORD, mSmtpPassword);
        intent.putExtra(PREF_SEND_MAIL, mSendMail);
        mAlarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mAlarmIntent);
    }

    private void cancelBroadcastEmailAlarm() {
        if(mAlarmManager!=null) {
            mAlarmIntent.cancel();
            mAlarmManager.cancel(mAlarmIntent);
        }
    }

    private boolean sendMail(String fromDate, String toDate) {
        // populate data
        String lines = mDbHelper.populateAttendance(fromDate, toDate);
        if(lines.isEmpty()) {
            return false;
        }
        final String content = lines;
        String subject1 = "Alpharai Absensi " + fromDate + "-" + toDate;
        subject1 = subject1.replace("-", " sd ");
        final String subject = subject1.replace("/", "-");
        SendEmailService.getInstance(
                mSmtpHost, mSmtpPort, mSmtpSender, mSmtpReceiver,
                mSmtpPassword).emailExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SendEmailService.getInstance(
                        mSmtpHost, mSmtpPort, mSmtpSender, mSmtpReceiver,
                        mSmtpPassword).SendEmail(subject, content);
            }
        });
        return true;
    }

    public static final String CHANNEL_ID_EMAIL = "channel_id_email_notif";
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_EMAIL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void onSettingSyncClick() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_sync_id, null);
        NoboButton btnSave = dialogView.findViewById(R.id.btn_sync_save);
        NoboButton btnSync = dialogView.findViewById(R.id.btn_sync_start);
        NoboButton btnCancel = dialogView.findViewById(R.id.btn_sync_cancel);
        final EditText edtSync = dialogView.findViewById(R.id.edt_sync);
        final EditText edtIp = dialogView.findViewById(R.id.edt_ip);
        edtSync.setText(Integer.toString(Prefs.getInt(PREF_SYNC_TIMEOUT, 30)));
        String[] ip = mIpAndPort.split(":");
        edtIp.setText(ip[0]);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textIp = edtIp.getText().toString().trim();
                if(textIp.isEmpty()) {
                    displayToastError(null, "IP tidak boleh kosong");
                    return;
                }
                displayProgressSpinKit();
                String finalTextIp = textIp + ":" + SERVER_PORT;
                // ping the address
                AsyncHttpClient client = new AsyncHttpClient();
                client.setMaxRetriesAndTimeout(1,1000);
                client.setResponseTimeout(1000);
                client.setConnectTimeout(1000);
                String url = URL_HTTP + finalTextIp + URL_GET_PING;
                client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Prefs.putString(PREF_DB_IP, finalTextIp);
                        mIpAndPort = finalTextIp;
                        Prefs.putString(PREF_DB_IP, finalTextIp);
                        String textTimeout = edtSync.getText().toString().trim();
                        mSyncTimeout = Integer.parseInt(textTimeout);
                        Prefs.putInt(PREF_SYNC_TIMEOUT, mSyncTimeout);
                        startSyncTimer();
                        displayToastSuccess("Pengaturan berhasil disimpan");
                        dialog.dismiss();
                        hideBlurBackground();
                        hideProgressSpinKit();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        displayToastError(null, "Server tidak terhubung, alamat IP tidak berhasil disimpan");
                        hideProgressSpinKit();
                    }
                });

            }
        });
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncNow();
                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void onSettingFaceDetection() {

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_face_id, null);
        dialog.setView(dialogView);
        NoboButton btnSave = dialogView.findViewById(R.id.btn_face_save);
        NoboButton btnCancel = dialogView.findViewById(R.id.btn_face_cancel);
        String stringTimeout = Integer.toString(mFdTimeout);
        final EditText edtTimoutFace = dialogView.findViewById(R.id.edt_timeout_face);
        edtTimoutFace.setText(stringTimeout);
        NiceSpinner niceSpinner = dialogView.findViewById(R.id.spinner_distance_face);
        List<String> dataset = new LinkedList<>(Arrays.asList("Dekat", "Sedang", "Jauh"));
        niceSpinner.attachDataSource(dataset);
        int distanceFace = Prefs.getInt(PREF_FD_DISTANCE, FACE_MEDIUM);
        niceSpinner.setSelectedIndex(distanceFace);
        CheckBox ckBB = dialogView.findViewById(R.id.ck_bb);
        ckBB.setChecked(Prefs.getBoolean(PREF_DRAW_BOUNDINGBOX, true));
        CheckBox ckLiveness = dialogView.findViewById(R.id.ck_liveness);
        ckLiveness.setChecked(Prefs.getBoolean(PREF_LIVENESS_MANDATORY, true));
        // by default, if it is 64Bit device, Liveness is mandatory
        if(mSupport64bit) ckLiveness.setVisibility(View.GONE);
        EditText edtStandbyTimeout = dialogView.findViewById(R.id.edt_standby_timeout);
        edtStandbyTimeout.setText(Integer.toString(mStandbyTimeout));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sTimeout = edtTimoutFace.getText().toString().trim();
                mFdTimeout = Integer.parseInt(sTimeout);
                String standby = edtStandbyTimeout.getText().toString().trim();
                mStandbyTimeout = Integer.parseInt(standby);
                mFaceDistanceCategory = niceSpinner.getSelectedIndex();
                mFlagDrawBoundingBox = ckBB.isChecked();
                mLivenessIsMandatory = ckLiveness.isChecked();
                Prefs.putInt(PREF_FD_TIMEOUT, mFdTimeout);
                Prefs.putInt(PREF_FD_DISTANCE, mFaceDistanceCategory);
                Prefs.putBoolean(PREF_DRAW_BOUNDINGBOX, mFlagDrawBoundingBox);
                Prefs.putInt(PREF_STANDBY_TIMER, mStandbyTimeout);
                Prefs.putBoolean(PREF_LIVENESS_MANDATORY, mLivenessIsMandatory);
                if(!mFlagDrawBoundingBox) clearBB();
                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                hideBlurBackground();
            }
        });
        dialog.show();
    }

    private synchronized void livenessDetection(DetectionProto.Detection detection) {
        // extract important keypoints
        float previewWidth = (float) mPreviewDisplayView.getWidth();
        float previewHeight = (float) mPreviewDisplayView.getHeight();
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
                    final long start = SystemClock.uptimeMillis();
                    // since tensorflow input image is scaled down, so we need to scale it up
                    // to get proper face position
                    Matrix matrixScale = new Matrix();
                    matrixScale.setScale(previewWidth/ mTensorImageSize, 1,
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
                    float deltax = DELTA_X_FACE * abs(right_eye[0] - left_eye[0]);
                    float deltay = DELTA_Y_FACE * abs(right_eye[0] - left_eye[0]);
                    float[] bb_left_top_1 = {left_eye[0] - deltax, left_eye[1] - deltay};
                    float[] bb_right_top_1 = {right_eye[0] + deltax, right_eye[1] - deltax};
                    float[] bb_left_bottom_1 = {left_eye[0] - deltax, mouth[1] + deltax};

                    // re-calculate the size
                    float crop_w = bb_right_top_1[0] - bb_left_top_1[0];
                    float crop_h = bb_left_bottom_1[1] - bb_left_top_1[1];

                    Bitmap croppedBitmap = Bitmap.createBitmap(rotateBitmap,
                            (int) bb_left_top_1[0],
                            (int) bb_left_top_1[1],
                            (int) crop_w,
                            (int) crop_h
                    );
                    int w = 160;
                    int h = round(160f * crop_h / crop_w);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap,w,h,false);

                    setBitmap(croppedBitmap);
                    mCroppedBitmap = croppedBitmap;
                    byte[] bArray = Utils.getBitmapAsByteArray(scaledBitmap);
                    String string64 = Utils.getByteArrayAsString64(bArray);
                    if(mSupport64bit) {
                        PyObject ret = mPyObjectLiveness.callAttr("isFakePrint", string64);
                        Log.d(TAG, "SPOOF: " + ret.toString());
                        showDebug("Liveness Score: " + ret.toString());
                        Message msg = mHandlerTimer.obtainMessage();
                        msg.what = THREAD_LIVENESS;
                        Bundle b = new Bundle();
                        if (ret.toFloat() <= mThresholdLiveness) {
                            b.putString(FACE_STATUS, FACE_REAL);
                        } else {
                            b.putString(FACE_STATUS, FACE_FAKE);
                        }
                        msg.setData(b);
                        mHandlerTimer.sendMessage(msg);
                    } else {
                        doPingAndLiveness(string64);
                    }

                    final long stop = SystemClock.uptimeMillis();
                    long timecost = stop - start;
                    Log.d(TAG, "Time cost: " + timecost + " milliseconds");
                    handlerThread.quitSafely();
                } catch (Exception ex) {
                    handlerThread.quitSafely();
                }
            }
        }, new Handler(handlerThread.getLooper()));
    }

    private void resetLivenessVariables() {
        mLivenessFrameCounter = 0;
        mLivenessRealCounter = 0;
        mLivenessFakeCounter = 0;
        mLivenessUnavailCounter = 0;
        mTimerFDAndLivenessIsFinished = true;
    }

    private boolean mLivenessIsMandatory = true;
    private void doPingAndLiveness(String faceString) {
        showDebug("doPingAndLiveness");
        mIsProcessing = true;
        startAnim = true;
//        displayProgressSpinKit();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1,1000);
        client.setResponseTimeout(1000);
        client.setConnectTimeout(1000);
        String url = URL_HTTP + mIpAndPort + URL_GET_PING;
        showDebug(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // online, continue do liveness detection
                requestLivenessToServer(faceString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // offline,
                if(!mLivenessIsMandatory) {
                    // if liveness is not mandatory, then continue by always assuming incoming image is real
                    broadcastLiveness(FACE_REAL);
                } else {
                    // liveness is mandatory so process cannot continue
                    // TODO: set necessary flags here so that attendance process cannot be continued

                }
            }
        });
    }

    private void requestLivenessToServer(String faceString) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1,1000);
        client.setResponseTimeout(1000);
        client.setConnectTimeout(1000);
        RequestParams rparams = new RequestParams();
        rparams.put("thumb", faceString);
        String url = URL_HTTP + mIpAndPort + URL_GET_LIVENESS;
        showDebug(url);
        client.get(url, rparams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject jo = null;
                try {
                    jo = new JSONObject(response);
                    String strscore = jo.getString("score");
                    float score = Float.parseFloat(strscore);
                    if(score<=mThresholdLiveness) {
                        broadcastLiveness(FACE_REAL);
                    } else {
                        broadcastLiveness(FACE_FAKE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void broadcastLiveness(String livenessStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandlerTimer.obtainMessage();
                msg.what = THREAD_LIVENESS;
                Bundle b = new Bundle();
                b.putString(FACE_STATUS, livenessStatus);
                msg.setData(b);
                mHandlerTimer.sendMessage(msg);
            }
        });
    }

    private Bitmap mBitmapScreen = null;
    private Bitmap mBitmapCopy = null;
    private void copyScreen() {
        if(mPreviewDisplayView==null) return;
        if(!mIsProcessing) return;
        float previewWidth = (float) mPreviewDisplayView.getWidth();
        float previewHeight = (float) mPreviewDisplayView.getHeight();
        // copy Bitmap
        mBitmapCopy = Bitmap.createBitmap(
                (int)previewWidth,
                (int)previewHeight,
                Bitmap.Config.ARGB_8888);
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        PixelCopy.request(mPreviewDisplayView, mBitmapCopy, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int i) {
                mBitmapScreen = mBitmapCopy;
            }
        }, new Handler(handlerThread.getLooper()));
    }

    private PaintView mPaintView;
    private boolean mFlagBoundingBoxProcessIsFinished = true;
    private boolean mFaceInArea = false;
    private void evaluateFaceOnThread(final DetectionProto.Detection detection) {
        if(!mFlagBoundingBoxProcessIsFinished) return;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mFlagBoundingBoxProcessIsFinished = false;
                evaluateFace(detection);
            }
        });
        thread.start();
//        thread.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        stopStandbyTimer();
        return super.dispatchTouchEvent(ev);
    }

    private boolean mRequestLiveness = false;
    private Bitmap mBitmapBig;
    private void evaluateFace(DetectionProto.Detection detection) {
        if(mPreviewDisplayView==null) return;
        float previewWidth = (float) mPreviewDisplayView.getWidth();
        float previewHeight = (float) mPreviewDisplayView.getHeight();
        //
        float[] bb_left_top = {detection.getLocationData().getRelativeBoundingBox().getXmin()*previewWidth,
                detection.getLocationData().getRelativeBoundingBox().getYmin()*previewHeight};
        float[] bb_right_top = {(detection.getLocationData().getRelativeBoundingBox().getXmin() +
                detection.getLocationData().getRelativeBoundingBox().getWidth())*previewWidth,
                detection.getLocationData().getRelativeBoundingBox().getYmin()*previewHeight};
        float[] bb_left_bottom = {detection.getLocationData().getRelativeBoundingBox().getXmin()*previewWidth,
                (detection.getLocationData().getRelativeBoundingBox().getYmin() +
                        detection.getLocationData().getRelativeBoundingBox().getHeight())*previewHeight};
        float[] bb_right_bottom = {(detection.getLocationData().getRelativeBoundingBox().getXmin() +
                detection.getLocationData().getRelativeBoundingBox().getWidth())*previewWidth,
                (detection.getLocationData().getRelativeBoundingBox().getYmin() +
                        detection.getLocationData().getRelativeBoundingBox().getHeight())*previewHeight};
        // copy Bitmap
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
                    // since tensorflow input image is scaled down, so we need to scale it up
                    // to get proper face position
                    Matrix matrixScale = new Matrix();
                    matrixScale.setScale(previewWidth/ mTensorImageSize, 1,
                            previewWidth/2, previewHeight/2);
                    matrixScale.mapPoints(bb_left_top);
                    matrixScale.mapPoints(bb_left_bottom);
                    matrixScale.mapPoints(bb_right_bottom);
                    matrixScale.mapPoints(bb_right_top);

                    float faceArea = (bb_right_top[0]-bb_left_top[0]) * (bb_left_bottom[1]-bb_left_top[1]);
                    float surfaceArea = previewHeight * previewWidth;

                    mBitmapBig = Bitmap.createBitmap(bitmapCopy, round(bb_left_top[0]), round(bb_left_top[1]),
                            round(bb_right_top[0]-bb_left_top[0]), round(bb_left_bottom[1]-bb_left_top[1]));

                    float faceAreaThreshold = mFaceAreas[mFaceDistanceCategory];
                    if(faceArea > (surfaceArea * faceAreaThreshold)) {
                        mFaceInArea = true;

//                        int width = round(2.0f*(bb_right_top[0]-bb_left_top[0])); //round(DeviceDimensionsHelper.convertDpToPixel(bb_right_top[0]-bb_left_top[0], mContext));
//                        int height = round(2.5f*(bb_left_bottom[1]-bb_left_top[1])); //round(DeviceDimensionsHelper.convertDpToPixel(bb_left_bottom[1]-bb_left_top[1], mContext));
//                        int X = round(bb_left_top[0])-10; //round(DeviceDimensionsHelper.convertDpToPixel(bb_left_top[0], mContext));
                        int Y = round(bb_left_top[1])-120; //round(DeviceDimensionsHelper.convertDpToPixel(bb_left_top[1], mContext));
                        if(startAnim) {
                            showScanAnim(Y);
                            clearBB();
                        } else {
                            drawBB(Color.GREEN, bb_left_top, bb_right_top, bb_right_bottom, bb_left_bottom);
                        }
                        stopStandbyTimer();
//                        showDebug("Put on for Screen...");
                        screenOn();
                        if(mRequestLiveness)
                            startLivenessThread();
                        if(!mIsProcessing && mTimerFDAndLivenessIsFinished) {
                            showDebug("mIsProcessing is false and TimerLiveness finished");
                            mTimerFDAndLivenessIsFinished = false;
                            startFDTimer();
                            mFaceLivenessStatus = "NA";
                            mRequestLiveness = true;
                        } else {
//                            showDebug("either mIsProcessing is true or TimerLiveness not finish");
//                            showDebug("mIsProcessing: " + Boolean.toString(mIsProcessing));
//                            showDebug("mTimerAndLivenessIsFinished: " + Boolean.toString(mTimerFDAndLivenessIsFinished));
                        }

                    } else {
                        mFaceInArea = false;
                        drawBB(Color.WHITE, bb_left_top, bb_right_top, bb_right_bottom, bb_left_bottom);
                        hideScanAnim();
                        startStandbyTimer();
                        stopFDTimer();
                        resetLivenessVariables();
                    }
                    if(!mIsStart)
                        clearBB();
                    mFlagBoundingBoxProcessIsFinished = true;
                    handlerThread.quitSafely();
                } catch (Exception ex) {
                    clearBB();
//                    showDebug(ex.toString());
                    mFlagBoundingBoxProcessIsFinished = true;
                    handlerThread.quitSafely();
                }
            }
        }, new Handler(handlerThread.getLooper()));

    }

    private void drawBB(int color,
                        float[] bb_left_top, float[] bb_right_top,
                        float[] bb_right_bottom, float[] bb_left_bottom) {
        if(!mFlagDrawBoundingBox) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPaintView.clear();
                mPaintView.setPaintColor(color);
                mPaintView.drawRectangle(bb_left_top, bb_right_top, bb_right_bottom, bb_left_bottom);
            }
        });
    }

    private void processFrame(final DetectionProto.Detection detection, final boolean online) {
        if(!online) {
            showDebug("OFFLINE MODE");
            showTextProgress("Tidak terkoneksi dengan server, berganti ke mode offline");
        } else {
            showDebug("ONLINE MODE");
            showTextProgress("Terkoneksi dengan server");
        }
        runInBackground(new Runnable() {
            @Override
            public void run() {
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
                            showDebug("Image size of copy bitmap = " +
                                    bitmapCopy.getWidth() + "x" + bitmapCopy.getHeight());
                            // since tensorflow input image is scaled down, so we need to scale it up
                            // to get proper face position
                            Matrix matrixScale = new Matrix();
                            matrixScale.setScale(previewWidth/ mTensorImageSize, 1,
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
                            float deltax = DELTA_X_FACE * abs(right_eye[0] - left_eye[0]);
                            float deltay = DELTA_Y_FACE * abs(right_eye[0] - left_eye[0]);
                            float[] bb_left_top_1 = {left_eye[0] - deltax, left_eye[1] - deltay};
                            float[] bb_right_top_1 = {right_eye[0] + deltax, right_eye[1] - deltax};
                            float[] bb_left_bottom_1 = {left_eye[0] - deltax, mouth[1] + deltax};
                            // re-calculate the size
                            float crop_w = bb_right_top_1[0] - bb_left_top_1[0];
                            float crop_h = bb_left_bottom_1[1] - bb_left_top_1[1];

                            Bitmap croppedBitmap = Bitmap.createBitmap(rotateBitmap,
                                    (int) bb_left_top_1[0],
                                    (int) bb_left_top_1[1],
                                    (int) crop_w,
                                    (int) crop_h
                            );
                            showDebug("Image size of cropped bitmap = " +
                                    croppedBitmap.getWidth() + "x" + croppedBitmap.getHeight());
                            setBitmap(croppedBitmap);
                            // generate Face Embedding
                            mCroppedBitmap = croppedBitmap;
                            /**
                             * Face Area Limit, also as the distance measurement between face and tablet
                             */
                            if(!mFaceInArea) {
                                showDebug("Face not in area");
                                delayedFinishProcessFlag();
                                hideProgressSpinKit();
                                hideTextProgress();
                                return;
                            }
                            stopStandbyTimerForProcess();

                            if(online) {
                                predictOnline(croppedBitmap);
                            } else {
                                predictOffline(croppedBitmap);
                            }
                        } catch (Exception ex) {
                            // face is not in the center of screen,
                            // half cropped, this will lead to exception,
                            // because coordinate inconsistence
//                            showDebug(ex.toString());
                            hideTextProgress();
                            delayedFinishProcessFlag();
                            hideProgressSpinKit();
                        }
                    }
                }, new Handler(handlerThread.getLooper()));
            }
        });
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (mHandlerMain != null) {
            mHandlerMain.post(r);
//            mThreadMainStatus = THREAD_RUN;
        }
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
                        Size displaySize = mCameraHelper.computeDisplaySizeFromViewSize(viewSize);

                        // Connect the converter to the camera-preview frames as its input (via
                        // previewFrameTexture), and configure the output width and height as the computed
                        // display size.
                        mConverter.setSurfaceTextureAndAttachToGLContext(
                                mPreviewFrameTexture, displaySize.getWidth(), displaySize.getHeight());
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        mProcessor.getVideoSurfaceOutput().setSurface(null);
                        holder.removeCallback(this);
                        mProcessor.close();
                        mProcessor = null;
                    }
                });
    }

    private void startCamera() {
        mCameraHelper = new OneCameraXPreviewHelper();
        mCameraHelper.setOnCameraStartedListener(new CameraHelper.OnCameraStartedListener() {
            @Override
            public void onCameraStarted(@Nullable SurfaceTexture surfaceTexture) {
                mPreviewFrameTexture = surfaceTexture;
                // Make the display view visible to start showing the preview. This triggers the
                // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
                mPreviewDisplayView.setVisibility(View.VISIBLE);
                showDebug("Camera started");
            }
        });
        mCameraHelper.startCamera(this, CAMERA_FACING, /*surfaceTexture=*/ null);
    }

    private void displayProgressSpinKit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressSpinKit.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayProgressGreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBarGreen.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressSpinKit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressSpinKit.setVisibility(View.GONE);
            }
        });
    }

    private void hideProgressGreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBarGreen.setVisibility(View.GONE);
            }
        });
    }

    private boolean syncProgressDone() {
        if(mUploadAttendance && mDownloadEncoding && mDownloadUser) {
            return true;
        }
        return false;
    }

    private void checkSyncAndStopProgress(boolean success) {
        if(syncProgressDone()) {
            enableMenu();
            hideProgressGreen();
            if(success)
//                displayToastSuccess("Database synced successfully.");
                displayToastSuccess("Berhasil mensinkronkan data dengan server.");
            else
                displayToastError(null,  "Gagal mensinkronkan data dengan server");

        }
    }

}
