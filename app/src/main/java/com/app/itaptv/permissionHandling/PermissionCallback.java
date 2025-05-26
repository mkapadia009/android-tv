package com.app.itaptv.permissionHandling;

/**
 * Created by Avinash on 12/20/2016
 */
public interface PermissionCallback {

    void onRequestPermissionGranted(String[] permission);

    void onRequestPermissionDenied(String[] permission);

    void onRequestPermissionDeniedPermanently(String[] permission);
}
