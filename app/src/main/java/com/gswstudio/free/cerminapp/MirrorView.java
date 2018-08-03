package com.gswstudio.free.cerminapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

/**
 * Created by Ganang on 7/10/2018.
 */

@SuppressLint("ViewConstructor")
public class MirrorView extends SurfaceView implements SurfaceHolder.Callback {

  public static final String TAG = "tag";

  private SurfaceHolder holder;
  private Camera camera;
  private Context context;

  public MirrorView(Context context, Camera camera) {
    super(context);
    this.context = context;
    this.camera = camera;
    holder = getHolder();
    holder.addCallback(this);
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    try {
      camera.setPreviewDisplay(holder);
      camera.startPreview();
    } catch (IOException e) {
      Log.d(TAG, "error starting PREVIEW LAYOUT: " + e.getMessage());
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    if (holder.getSurface() == null) {
      return;
    }

    try {
      camera.stopPreview();
    } catch (Exception e) {

    }

    try {
      camera.setPreviewDisplay(holder);
      camera.startPreview();
    } catch (IOException e) {
      Log.d(TAG, "error starting PREVIEW LAYOUT: " + e.getMessage());
    }
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {

  }

  public void setCameraDisplayOrientationAndSize(int mCameraId) {
    Camera.CameraInfo info = new Camera.CameraInfo();
    Camera.getCameraInfo(mCameraId, info);
    int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
    int degrees = rotation * 90;

    int result;
    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360;
      result = (360 - result) % 360;
    } else {
      result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);
    Camera.Size size = camera.getParameters().getPreviewSize();
    if (result == 90 || result == 270) {
      holder.setFixedSize(size.height, size.width);
    } else {
      holder.setFixedSize(size.width, size.height);
    }
  }


}
