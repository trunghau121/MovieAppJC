# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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


#====================================================================
# Modern R8 Optimizer Configuration
#====================================================================

# 🔎 Debugging & Logging Essentials
-verbose

# ➡️ Glide (Image Loading Optimization)
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
-keep class !com.bumptech.glide.repackaged.**,com.bumptech.glide.**

# Suppress Glide / JVM internal notes
-dontnote sun.applet.**
-dontnote sun.tools.jar.**
-dontnote com.bumptech.glide.repackaged.com.google.common.**

# ➡️ Network Layer (OkHttp & Dependencies)
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.codehaus.mojo.animal_sniffer.*

# ➡️ Retrofit & API Serialization Reflection
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ➡️ Moshi (JSON Serialization)
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# ➡️ Kotlin & Coroutines Environment
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { public <methods>; }
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ➡️ Jetpack Compose Core Internals
-keepclassmembers class androidx.compose.ui.platform.ViewLayerContainer {
    protected void dispatchGetDisplayList();
}
-keepclassmembers class androidx.compose.ui.platform.AndroidComposeView {
    android.view.View findViewByAccessibilityIdTraversal(int);
}
-keep,allowshrinking class * extends androidx.compose.ui.node.ModifierNodeElement

# ➡️ EncryptedSharedPreferences Support
-dontwarn com.google.api.client.http.**
-dontwarn org.joda.time.**

# ➡️ Data Models (Safeguarded for Reflection Engines)
-keepnames @kotlin.Metadata class com.movieappjc.data.models.**
-keep class com.movieappjc.data.models.** { *; }
-keepclassmembers class com.movieappjc.data.models.** { *; }