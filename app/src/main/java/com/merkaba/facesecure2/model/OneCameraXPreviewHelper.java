package com.merkaba.facesecure2.model;

import com.google.mediapipe.components.CameraHelper;
import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.UseCase;
import androidx.camera.core.CameraX.LensFacing;
import androidx.camera.core.PreviewConfig.Builder;
import androidx.lifecycle.LifecycleOwner;
import com.google.mediapipe.components.CameraHelper.CameraFacing;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class OneCameraXPreviewHelper extends CameraHelper {
    private static final String TAG = "CameraXPreviewHelper";
    private static final Size TARGET_SIZE = new Size(1280, 720);
    private static final int CLOCK_OFFSET_CALIBRATION_ATTEMPTS = 3;
    private Preview preview;
    private Size frameSize;
    private int frameRotation;
    @Nullable
    private CameraCharacteristics cameraCharacteristics = null;
    private float focalLengthPixels = 1.4E-45F;
    private int cameraTimestampSource = 0;

    public OneCameraXPreviewHelper() {
    }

    public void startCamera(Activity context, CameraFacing cameraFacing, SurfaceTexture surfaceTexture) {
        this.startCamera(context, cameraFacing, surfaceTexture, TARGET_SIZE);
    }

    public void startCamera(Activity context, CameraFacing cameraFacing, SurfaceTexture surfaceTexture, Size targetSize) {
        LensFacing cameraLensFacing = cameraFacing == CameraFacing.FRONT ? LensFacing.FRONT : LensFacing.BACK;
        PreviewConfig previewConfig = (new Builder()).setLensFacing(cameraLensFacing).setTargetResolution(targetSize).build();
        this.preview = new Preview(previewConfig);
        this.preview.setOnPreviewOutputUpdateListener((previewOutput) -> {
            if (!previewOutput.getTextureSize().equals(this.frameSize)) {
                this.frameSize = previewOutput.getTextureSize();
                this.frameRotation = previewOutput.getRotationDegrees();
                if (this.frameSize.getWidth() == 0 || this.frameSize.getHeight() == 0) {
                    Log.d("CameraXPreviewHelper", "Invalid frameSize.");
                    return;
                }
            }

            Integer selectedLensFacing = cameraFacing == CameraFacing.FRONT ? 0 : 1;
            this.cameraCharacteristics = getCameraCharacteristics(context, selectedLensFacing);
            if (this.cameraCharacteristics != null) {
                this.cameraTimestampSource = (Integer)this.cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE);
                this.focalLengthPixels = this.calculateFocalLengthInPixels();
            }

            if (this.onCameraStartedListener != null) {
                this.onCameraStartedListener.onCameraStarted(previewOutput.getSurfaceTexture());
            }

        });
        CameraX.bindToLifecycle((LifecycleOwner)context, new UseCase[]{this.preview});
    }

    public void unBind() {
        CameraX.unbindAll();
    }

    public boolean isCameraRotated() {
        return this.frameRotation % 180 == 90;
    }

    public Size computeDisplaySizeFromViewSize(Size viewSize) {
        if (viewSize != null && this.frameSize != null) {
            Size optimalSize = this.getOptimalViewSize(viewSize);
            return optimalSize != null ? optimalSize : this.frameSize;
        } else {
            Log.d("CameraXPreviewHelper", "viewSize or frameSize is null.");
            return null;
        }
    }

    @Nullable
    private Size getOptimalViewSize(Size targetSize) {
        if (this.cameraCharacteristics != null) {
            StreamConfigurationMap map = (StreamConfigurationMap)this.cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] outputSizes = map.getOutputSizes(SurfaceTexture.class);
            int selectedWidth = -1;
            int selectedHeight = -1;
            float selectedAspectRatioDifference = 1000.0F;
            float targetAspectRatio = (float)targetSize.getWidth() / (float)targetSize.getHeight();
            Size[] var8 = outputSizes;
            int var9 = outputSizes.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                Size size = var8[var10];
                float aspectRatio = (float)size.getWidth() / (float)size.getHeight();
                float aspectRatioDifference = Math.abs(aspectRatio - targetAspectRatio);
                if (aspectRatioDifference <= selectedAspectRatioDifference && (selectedWidth == -1 && selectedHeight == -1 || size.getWidth() <= selectedWidth && size.getWidth() >= this.frameSize.getWidth() && size.getHeight() <= selectedHeight && size.getHeight() >= this.frameSize.getHeight())) {
                    selectedWidth = size.getWidth();
                    selectedHeight = size.getHeight();
                    selectedAspectRatioDifference = aspectRatioDifference;
                }
            }

            if (selectedWidth != -1 && selectedHeight != -1) {
                return new Size(selectedWidth, selectedHeight);
            }
        }

        return null;
    }

    public long getTimeOffsetToMonoClockNanos() {
        return this.cameraTimestampSource == 1 ? getOffsetFromRealtimeTimestampSource() : getOffsetFromUnknownTimestampSource();
    }

    private static long getOffsetFromUnknownTimestampSource() {
        return 0L;
    }

    private static long getOffsetFromRealtimeTimestampSource() {
        long offset = 9223372036854775807L;
        long lowestGap = 9223372036854775807L;

        for(int i = 0; i < 3; ++i) {
            long startMonoTs = System.nanoTime();
            long realTs = SystemClock.elapsedRealtimeNanos();
            long endMonoTs = System.nanoTime();
            long gapMonoTs = endMonoTs - startMonoTs;
            if (gapMonoTs < lowestGap) {
                lowestGap = gapMonoTs;
                offset = (startMonoTs + endMonoTs) / 2L - realTs;
            }
        }

        return offset;
    }

    public float getFocalLengthPixels() {
        return this.focalLengthPixels;
    }

    public Size getFrameSize() {
        return this.frameSize;
    }

    private float calculateFocalLengthInPixels() {
        float focalLengthMm = ((float[])this.cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS))[0];
        float sensorWidthMm = ((SizeF)this.cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)).getWidth();
        return (float)this.frameSize.getWidth() * focalLengthMm / sensorWidthMm;
    }

    @Nullable
    private static CameraCharacteristics getCameraCharacteristics(Activity context, Integer lensFacing) {
        CameraManager cameraManager = (CameraManager)context.getSystemService("camera");

        try {
            List<String> cameraList = Arrays.asList(cameraManager.getCameraIdList());
            Iterator var4 = cameraList.iterator();

            while(var4.hasNext()) {
                String availableCameraId = (String)var4.next();
                CameraCharacteristics availableCameraCharacteristics = cameraManager.getCameraCharacteristics(availableCameraId);
                Integer availableLensFacing = (Integer)availableCameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (availableLensFacing != null && availableLensFacing.equals(lensFacing)) {
                    return availableCameraCharacteristics;
                }
            }
        } catch (CameraAccessException var8) {
            Log.e("CameraXPreviewHelper", "Accessing camera ID info got error: " + var8);
        }

        return null;
    }
}


