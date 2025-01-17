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
# Keep Gson classes
#-keep class com.google.gson.** { *; }
#
## Keep Lottie classes
#-keep class com.airbnb.lottie.** { *; }
#
## Keep Android support libraries
#-keep class androidx.** { *; }
#-keep class android.support.** { *; }
#
## Keep all public classes and methods
#-keep public class * {
#    public *;
#}
## Keep javax.annotation.Nullable
#-keep class javax.annotation.Nullable { *; }
#
## Keep javax.lang.model.element.Modifier
#-keep class javax.lang.model.element.Modifier { *; }
#
## Keep Okio classes
#-keep class okio.** { *; }
#-dontwarn okio.**
#
## Keep Error Prone annotations
#-keep class com.google.errorprone.annotations.** { *; }
#-dontwarn com.google.errorprone.annotations.**