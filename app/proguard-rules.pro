# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# PAYTM PROGUARD
-keepclassmembers class com.paytm.pgsdk.PaytmWebView$PaytmJavaScriptInterface {
   public *;
}

# RAZORPAY
-keepattributes *Annotation*
-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}
-optimizations !method/inlining/
-keepclasseswithmembers class * {
  public void onPayment*(...);
}

# FIREWORKS
-keepclassmembers class com.loopnow.fireworklibrary.** { <fields>; }

# Coupon Dunia
-keep class com.app.itaptv.structure.vouchers.** { *; }

  -keep class com.trackier.sdk.** { *; }
  -keep class com.google.android.gms.common.ConnectionResult {
      int SUCCESS;
  }
  -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
      com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
  }
  -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
      java.lang.String getId();
      boolean isLimitAdTrackingEnabled();
  }
  -keep public class com.android.installreferrer.** { *; }

  -keep class io.flutter.app.** { *; }
  -keep class io.flutter.plugin.**  { *; }
  -keep class io.flutter.util.**  { *; }
  -keep class io.flutter.view.**  { *; }
  -keep class io.flutter.**  { *; }
  -keep class io.flutter.plugins.**  { *; }
  -keep class io.flutter.plugin.editing.** { *; }