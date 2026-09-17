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
# R8 / ProGuard Configuration - Library Consumer Rules
#====================================================================

# Protect the module's own package tree from being wiped out by structural optimization
-keep class com.core_app.** { *; }
-keepclassmembers class com.core_app.** { *; }
-dontwarn com.core_app.**

# ➡️ Glide Rules
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
-dontnote sun.applet.**
-dontnote sun.tools.jar.**
-dontnote com.bumptech.glide.repackaged.com.google.common.**

# ➡️ Networking & Parsing Dependencies
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.codehaus.mojo.animal_sniffer.*

# ➡️ Retrofit & Moshi Reflection Preservation
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# ➡️ Security Core
-dontwarn com.google.api.client.http.**
-dontwarn org.joda.time.**

# ➡️ Kotlin & Compose Lifecycle Meta
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { public <methods>; }
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keepclassmembers class androidx.compose.ui.platform.ViewLayerContainer {
    protected void dispatchGetDisplayList();
}
-keepclassmembers class androidx.compose.ui.platform.AndroidComposeView {
    android.view.View findViewByAccessibilityIdTraversal(int);
}
-keep,allowshrinking class * extends androidx.compose.ui.node.ModifierNodeElement

# ➡️ Dagger / Hilt Framework Rules
-keep class dagger.** { *; }
-dontwarn dagger.**
-keep class javax.inject.** { *; }
-keep interface dagger.hilt.** { *; }

# Keep injected fields and constructor properties visible
-keep class * {
    @dagger.** <fields>;
    @javax.inject.** <fields>;
    @dagger.hilt.android.plugin.* <methods>;
}

# Explicitly protect standard Android component classes from deletion
-keepclassmembers class * extends android.app.Application {
    public void onCreate();
}

