# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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
-optimizationpasses 7   # 指定代码的压缩级别
#google推荐算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-keep class com.qingniu.scale.model.BleScaleData{*;}
-keep class com.xiekang.bluetooths.interfaces.* {*;}
-keep class com.xiekang.bluetooths.bluetooths.oxgen.draw.* {*;}
-keep class com.creative.base.*{*;}
-keep class com.xiekang.bluetooths.bean.* {*;}
-keep class com.xiekang.bluetooths.BluetoothMangers{
      public <methods>;
      }
-keep class com.xiekang.bluetooths.bluetooths.QNBleDeviceUtlis{
      public <methods>;
      }
