#include <jni.h>

//For first API key
JNIEXPORT jstring JNICALL
Java_com_d2dreamdevelopers_myapplication_APIKeyLibrary_getAPIKey(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "YOUR_AWESOME_API_GOES_HERE");
}

JNIEXPORT jstring JNICALL
Java_com_app_itaptv_API_Url_getDevKeys(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "https://itap.inspeero.com/");
}

JNIEXPORT jstring JNICALL
Java_com_app_itaptv_API_Url_getProdKeys(JNIEnv *env, jclass clazz) {
    return (*env)-> NewStringUTF(env, "https://app.itap.online/");
}

JNIEXPORT jstring JNICALL
Java_com_app_itaptv_utils_Constant_getSecretKey(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "6bfdf119e98a31822345619c65068924");
}

JNIEXPORT jstring JNICALL
Java_com_app_itaptv_utils_Constant_getIvParameter(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "8957854b2494ebdc");
}

JNIEXPORT jstring JNICALL
Java_com_app_itaptv_utils_Constant_getSecretKeyDateTime(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "ae03deaee6271c66f3bf4de4bc6e572b");
}

JNIEXPORT jstring JNICALL
Java_com_app_itaptv_utils_Constant_getIvParameterDateTime(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "5c089971102755c6");
}

JNIEXPORT jstring JNICALL
Java_com_app_itaptv_utils_Constant_getTestPublishKey(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "pk_test_51LKbc9SGSP1VX1aOepwpLQ23LbWXzF7JSMXF3tWVebaXEegcOtl6QWiYLESoQjO17VB5O5JpnpkrYMZB3SyEUKcL00Fwt18npm");
}

JNIEXPORT jstring JNICALL
Java_com_app_itaptv_utils_Constant_getLivePublishKey(JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "pk_live_51LKbc9SGSP1VX1aO2zmnwy9imYgrN8JXTBHjtIzYI3zm9k2xEVkThp2ouewdrzuZkWqnQyJdRscaLS3mDRqnBIQ900d3APh4KK");
}