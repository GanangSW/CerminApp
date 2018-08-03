package com.gswstudio.free.cerminapp;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

  private Camera camera;
  private MirrorView mirrorView;
  private int mCameraId = 0;
  private FrameLayout layout;



  @SuppressLint("WrongConstant")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mirrorView = new MirrorView(this, camera);
    mCameraId = findFirstFrontFacingCamera();

    layout = findViewById(R.id.camPreview);
    layout.removeAllViews();

    int hasWriteStoragePermission = 0;
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      hasWriteStoragePermission = checkSelfPermission(permission.CAMERA);
    }

    if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        int REQUST_CODE_CAMERA = 1;
        requestPermissions(new String[]{permission.CAMERA}, REQUST_CODE_CAMERA);
      }
    }

    startCameraInLayout(layout, mCameraId);
    mirrorView.setCameraDisplayOrientationAndSize(mCameraId);
  }

  private void startCameraInLayout(FrameLayout layout, int id) {
    camera = Camera.open(id);
    if (camera != null) {
      mirrorView = new MirrorView(this, camera);
      layout.addView(mirrorView);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (camera == null && layout != null) {
      layout.removeAllViews();
      startCameraInLayout(layout, mCameraId);
    }
  }

  @Override
  protected void onPause() {
    if (camera != null) {
      camera.release();
      camera = null;
    }
    super.onPause();
  }

  private int findFirstFrontFacingCamera() {
    int foundId = -1;
    int numCams = Camera.getNumberOfCameras();
    for (int camId = 0; camId < numCams; camId++) {
      Camera.CameraInfo info = new Camera.CameraInfo();
      Camera.getCameraInfo(camId, info);
      if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
        foundId = camId;
        break;
      }
    }
    return foundId;
  }
}
