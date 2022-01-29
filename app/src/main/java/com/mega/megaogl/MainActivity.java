package com.mega.megaogl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mega.megaogl.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class MainActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    public static Context appContext;
    public static Renderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setEGLContextClientVersion(2);
            renderer = new Renderer();
            glSurfaceView.setRenderer(renderer);

        } else {
            return;
        }
        setContentView(glSurfaceView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
}