/*
 * Copyright (c) 2014 Rex St. John on behalf of AirPair.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.discoverylab.ripple.android.fragment;


import android.app.Fragment;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Fragment to display a camera preview and capture button.
 * <p/>
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Reference: https://github.com/rexstjohn/UltimateAndroidCameraGuide/blob/master/camera/src/main/java/com/ultimate/camera/fragments/NativeCameraFragment.java
 * <p/>
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 * Modified by James West in July 2014
 */
public class CameraFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CameraFragment.class.getSimpleName();
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    // View holding camera
    private View mCameraView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CameraFragment.
     */
    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        boolean opened = safeCameraOpenInView(v);

        if (!opened) {
            Log.e(TAG, "Error, failed to open camera");
            return v;
        }

        Button capture = (Button) v.findViewById(R.id.camera_capture);
        capture.setOnClickListener(this);

        return v;
    }

    /**
     * Recommended "safe" way to open the camera.
     *
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        mCameraView = view;
        qOpened = (mCamera != null);

        if (mCamera != null) {
            mCameraPreview = new CameraPreview(getActivity().getBaseContext(), mCamera, view);
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
            preview.addView(mCameraPreview);
            mCameraPreview.startCameraPreview();
        }
        return qOpened;
    }

    /**
     * Safe method for getting a camera instance.
     *
     * @return
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
    }

    /**
     * Clear any existing preview / camera.
     */
    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mCameraPreview != null) {
            mCameraPreview.destroyDrawingCache();
            mCameraPreview.camera = null;
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.camera_capture) {
            return;
        }

        mCamera.takePicture(null, null, pictureCallback);

    }


    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {


        private final String TAG = CameraPreview.class.getSimpleName();
        private SurfaceHolder surfaceHolder;

        private Camera.Size previewSize;

        private Camera camera;

        private View cameraView;

        /**
         * @param context    Context for view
         * @param camera     Camera to preview
         * @param cameraView View holding the camera
         */
        public CameraPreview(Context context, Camera camera, View cameraView) {
            super(context);

            setCamera(camera);
            this.cameraView = cameraView;

            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setKeepScreenOn(true);
        }

        public void startCameraPreview() {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "Error starting camera preview.");
            }
        }

        /**
         * Extract supported preview and flash modes from the camera.
         *
         * @param camera
         */
        private void setCamera(Camera camera) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            this.camera = camera;
            List<String> mSupportedFlashModes = camera.getParameters().getSupportedFlashModes();

            // Set the camera to Auto Flash mode.
            if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = this.camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                this.camera.setParameters(parameters);
            }

            requestLayout();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                Log.e(TAG, "Failed to set preview display.");
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            // make sure surface exists
            if (surfaceHolder.getSurface() == null) {
                return;
            }

            try {
                Camera.Parameters parameters = this.camera.getParameters();

                // set to auto focus continuous
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                // set preview size
                if (previewSize != null) {
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                }

                camera.setParameters(parameters);

                camera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "Failed to setup camera on surface changed.");
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
            }
        }

        /**
         * Calculate the measurements of the layout
         *
         * @param widthMeasureSpec
         * @param heightMeasureSpec
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            previewSize = getOptimalPreviewSize(width, height);

        }

        /**
         * Update the layout based on rotation and orientation changes.
         *
         * @param changed
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            if (changed) {
                final int width = right - left;
                final int height = bottom - top;

                int previewWidth = width;
                int previewHeight = height;

                if (previewSize != null) {
                    Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                    switch (display.getRotation()) {
                        case Surface.ROTATION_0:
                            previewWidth = previewSize.height;
                            previewHeight = previewSize.width;
                            this.camera.setDisplayOrientation(90);
                            break;
                        case Surface.ROTATION_90:
                            previewWidth = previewSize.width;
                            previewHeight = previewSize.height;
                            break;
                        case Surface.ROTATION_180:
                            previewWidth = previewSize.height;
                            previewHeight = previewSize.width;
                            break;
                        case Surface.ROTATION_270:
                            previewWidth = previewSize.width;
                            previewHeight = previewSize.height;
                            this.camera.setDisplayOrientation(180);
                            break;
                    }
                }

                final int scaledChildHeight = previewHeight * width / previewWidth;
                this.cameraView.layout(0, height - scaledChildHeight, width, height);
            }
        }

        /**
         * @param width
         * @param height
         * @return
         */
        private Camera.Size getOptimalPreviewSize(int width, int height) {
            List<Camera.Size> sizes = this.camera.getParameters().getSupportedPreviewSizes();
            ;

            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            Camera.Size optimalSize = null;

            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) height / width;

            // Try to find a size match which suits the whole screen minus the menu on the left.
            for (Camera.Size size : sizes) {

                if (size.height != width) continue;
                double ratio = (double) size.width / size.height;
                if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                    optimalSize = size;
                }
            }

            // If we cannot find the one that matches the aspect ratio, ignore the requirement.
            if (optimalSize == null) {
                // TODO : Backup in case we don't get a size.
            }

            return optimalSize;
        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File outFile = getOutputPictureFile();
            if (outFile == null) {
                Toast.makeText(getActivity(), "Failed to save image.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(outFile);
                fos.write(data);
                fos.close();

                // restart camera preview ?
                safeCameraOpenInView(mCameraView);
            } catch (FileNotFoundException fe) {
                Log.e(TAG, "File not found exception when saving image.");
            } catch (IOException ie) {
                Log.e(TAG, "IO exception when saving image");
            }
        }
    };

    private File getOutputPictureFile() {
        // get the storage directory
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Ripple");

        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(TAG, "Storage directory does not exist!");
            }
        }

        // Create file
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outFile = new File(storageDir.getPath() + File.separator + "IMG_NOTE_" + timestamp + ".jpg");

        return outFile;


    }
}
