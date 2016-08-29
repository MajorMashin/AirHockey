package com.mdsft.airhockey1;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class AirHockeyActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        final AirHockeyRenderer airHockeyRenderer = new AirHockeyRenderer(this);

        final ActivityManager activityManager =
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            // Assign the renderer.
            glSurfaceView.setRenderer(airHockeyRenderer);
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 if (event != null) {
                     final float normalizedX =
                         (event.getX() / (float) v.getWidth()) * 2 - 1;
                     final float normalizedY =
                         -((event.getY() / (float) v.getHeight()) * 2 - 1);

                     if (event.getAction() == MotionEvent.ACTION_DOWN) {
                         glSurfaceView.queueEvent(new Runnable() {
                             @Override
                             public void run() {
                                 airHockeyRenderer.handleTouchPress(
                                     normalizedX, normalizedY);
                             }
                         });
                     } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                         glSurfaceView.queueEvent(new Runnable() {
                             @Override
                             public void run() {
                                 airHockeyRenderer.handleTouchDrag(
                                     normalizedX, normalizedY);
                             }
                         });
                     }
                     return true;
                 } else {
                     return false;
                 }
             }
         });
        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}
