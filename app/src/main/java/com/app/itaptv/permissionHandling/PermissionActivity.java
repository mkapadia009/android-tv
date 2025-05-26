package com.app.itaptv.permissionHandling;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Avinash on 12/20/2016
 */
public abstract class PermissionActivity extends AppCompatActivity implements PermissionCallback {

    private static final int MY_PERMISSIONS_REQUEST = 1;

    private PermissionCallback callback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = this;
        checkPermission();
    }

    public void checkPermission() {
        // check permissions in runtime from Android M (API Level 23)
        if (isCorrectApiLevel()) {
            checkClassPermissions();
        } else {
            // grant permission
            callback.onRequestPermissionGranted(getClassPermissions());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                callback.onRequestPermissionGranted(permissions);
            } else {
                boolean denied = false;
                for (String per : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, per)) {
                        denied = true;
                    }
                }
                if (denied) {
                    // permission denied
                    callback.onRequestPermissionDenied(permissions);
                } else {
                    // permission denied permanently
                    callback.onRequestPermissionDeniedPermanently(permissions);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionGranted(String[] permission) {
        // This method can be implemented by subclasses
    }

    @Override
    public void onRequestPermissionDenied(String[] permission) {
        // This method can be implemented by subclasses
    }

    @Override
    public void onRequestPermissionDeniedPermanently(String[] permission) {

    }

    public void askPermission(String[] permissions, PermissionCallback callback) {
        this.callback = callback;
        if (isCorrectApiLevel()) {
            askForPermissions(permissions);
        } else {
            // grant the permission
            callback.onRequestPermissionGranted(permissions);
        }
    }

    // permission
    public boolean isGrantedPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkClassPermissions() {
        // get current permissions
        String[] classPermissions = getClassPermissions();
        if (classPermissions != null) {
            askForPermissions(classPermissions);
        }
    }

    private void askForPermissions(String[] permissions) {
        // request permissions
        List<String> deniedPermissions = getDeniedPermissions(permissions);
        if (!deniedPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, deniedPermissions.toArray(new String[deniedPermissions.size()]), MY_PERMISSIONS_REQUEST);
        }
    }

    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (!isGrantedPermission(permissions[i])) {
                result.add(permissions[i]);
            }
        }
        return result;
    }

    private boolean isGrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isCorrectApiLevel() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private String[] getClassPermissions() {
        RegisterPermission registerPermission = this.getClass().getAnnotation(RegisterPermission.class);
        if (registerPermission != null) {
            return registerPermission.permissions();
        }
        return null;
    }
}
